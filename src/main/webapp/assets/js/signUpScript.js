// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// This file provides the JavaScript induced on the sign up page (sign-up.html) with
// form validation to impose strict restrictions on user input (from feedScript.js) and 
// college dropdown support (from indexScript.js).

//
// Event listener registration
//

// Hooks the onLoad function to the DOMContentLoaded event.
document.addEventListener('DOMContentLoaded', onLoad);

//
// Elements
//

/** @type {HTMLElement} */
let formSubmitted;

/** @type {HTMLElement} */
let formSubmittedTitle;

/** @type {HTMLElement} */
let submitFormButton;

/** @type {HTMLElement} */
let emailForm;

//
// Functions
//

/**
 * Fires as soon as the DOM is loaded.
 */
async function onLoad() {
  const collegeLocations = await (await fetch('./assets/college-locations.json')).json();

  // Grab the datalist and remove its ID (destroying the select-datalist relationship),
  // to improve performance while adding the options to the datalist.
  const collegeDataList = document.getElementById('colleges');
  collegeDataList.removeAttribute('id');

  // Add all colleges as datalist options. We use a document fragment because the
  // DOM is slow if we add each option individually and let the DOM update in between.
  const fragment = document.createDocumentFragment();
  collegeLocations.forEach((location) => {
    const newOption = document.createElement('option');
    newOption.setAttribute('data-value', location.UNITID);
    newOption.value = location.NAME;

    fragment.appendChild(newOption);
  });

  // Add the options and restore the select-datalist relationship.
  collegeDataList.appendChild(fragment);
  collegeDataList.setAttribute('id', 'colleges');

  // When users select an option from the dropdown, set the college id.
  document.getElementById('colleges-input-form').addEventListener('change', setCollegeID);

  submitFormButton = document.getElementById('form-submit');
  emailForm = document.getElementById('email-form');
  formSubmitted = document.getElementById('form-submitted');
  formSubmittedTitle = document.getElementById('form-submitted-title');
  submitFormButton.addEventListener('click', submitModal);
}

/**
 * Checks if the given input is blank.
 * @param {String} input
 * @return {void}
 */
function isBlank(input) {
  const trimmed = input.trim();
  return !trimmed;
}

/**
 * When the user selects a college from the dropdown, we immediately
 * set the ID to be that college's unique ID.
 */
function setCollegeID() {
  const collegeName = document.getElementById('colleges-input-form').value;
  const option = document.querySelector(`#colleges option[value='${collegeName}']`);

  // Set hidden input to hold unique college id.
  // TODO: display 'We haven't heard of that college!"/similar to the user if not recognized.
  if (option) {
    const collegeId = option.dataset.value;
    const selectedCollege = document.getElementById('cID');
    selectedCollege.value = collegeId;
  }
}

/**
 * On click of the submit button, sends modal data to the servlet.
 * @return {void}
 */
async function submitModal() {
  // Disable multiple submissions.
  submitFormButton.disabled = true;

  if (validateModal()) {
    checkLocationAndSubmit();
  } else {
    submitFormButton.disabled = false;
  }
}

/**
 * Checks if the month and day are a valid date.
 * @param {array} invalidIds
 * @param {array} errorMessages
 * @param {array} formElements
 * @return {void}
 */
function validateModalDate(invalidIds, errorMessages, formElements) {
  const month = formElements.namedItem('modal-month').value;
  const day = formElements.namedItem('modal-day').value;
  const monthDayLengths = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
  if (month > 12 || month < 1) {
    invalidIds.push('modal-month');
    errorMessages.push('month must be between 1 and 12');
  }
  if (day < 1 || day > monthDayLengths[month - 1] || isBlank(day)) {
    invalidIds.push('modal-day');
    errorMessages.push('invalid date');
  }
}


/**
 * Goes through the form elements and marks the invalid inputs.
 * Returns whether all the inputs are valid.
 * @return {boolean}
 */
function validateModal() {
  const invalidIds = [];
  const errorMessages = [];
  const formElements = emailForm.elements;

  disableInjection(formElements);
  validateModalText(invalidIds, errorMessages, formElements);
  markInvalidInputs(invalidIds, errorMessages, formElements);

  return (invalidIds.length === 0);
}

/**
 * Goes through the elements and encodes common HTML enities.
 * By using these codes, ensures that the user's inputs are seen as data, not code.
 * This is a measure against a malicious users trying to changing site data.
 * @param {array} formElements
 * @return {void}
 */
function disableInjection(formElements) {
  for (let i = 0; i < formElements.length - 1; i++) {
    let elementValue = formElements[i].value;
    elementValue = elementValue.replace(/</g, '&lt;').replace(/>/g, '&gt;');
    elementValue = elementValue.replace(/&/g, '&amp;');
    elementValue = elementValue.replace(/"/g, '&quot;');
    elementValue = elementValue.replace(/'/g, '&#x27;');
  }
}

/**
 * Goes through the form elements. If it is a text input, adds an error if it is blank.
 * @param {array} invalidIds
 * @param {array} errorMessages
 * @param {array} formElements
 * @return {void}
 */
function validateModalText(invalidIds, errorMessages, formElements) {
  for (let i = 0; i < formElements.length; i++) {
    if (formElements[i].type === 'text' || formElements[i].type === 'textarea') {
      if (isBlank(formElements[i].value)) {
        invalidIds.push(formElements[i].id);
        const errorMessage = formElements[i].placeholder + ' is blank';
        errorMessages.push(errorMessage);
      }
    }
  }
}

/**
 * Checks if the inputted address is valid.
 * Submits if address is verified.
 * @return {void}
 */
async function checkLocationAndSubmit() {

  url = `/user`;
  emailForm.action = url;

  // Hides form modal, shows submit message and focuses on it.
  modalCard.style.display = 'none';
  formSubmitted.style.display = 'block';
  // Closes the submit modal after 2000 ms, and submits the form.
  setTimeout(function() {
    closeSubmit();
    emailForm.submit();
  }, 2000);
}

/**
 * Closes the submit modal.
 * @return {void}
 */
function closeSubmit() {
  formSubmitted.style.display = 'none';
}

/**
 * Goes through the invalid inputs and colors them red.
 * Adds error messages.
 * @param {array} invalidIds
 * @param {array} errorMessages
 * @param {array} formElements
 * @return {void}
 */
function markInvalidInputs(invalidIds, errorMessages, formElements) {
  resetMarks(formElements);
  invalidIds.forEach((id) => {
    const elt = formElements.namedItem(id);
    if (elt) {
      elt.style.background = '#ff999966';
    }
  });
  if (errorMessages.length > 0) {
    const modalError = document.createElement('div');
    modalError.setAttribute('id', 'modal-input-error');
    modalError.setAttribute('tabindex', '0');
    modalError.innerHTML += '<ul>';
    errorMessages.forEach((message) => {
      modalError.innerHTML += '<li>' + message + '</li>';
    });
    modalError.innerHTML += '</ul>';
    emailForm.insertBefore(modalError, submitFormButton);
  }
}

/**
 * Resets all the input backgrounds to modal grey and gets rid of previous error text.
 * @param {array} formElements
 * @return {void}
 */
function resetMarks(formElements) {
  for (let i = 0; i < formElements.length - 1; i++) {
    const element = formElements[i];
    element.style.background = '#1b18181a';
  }
  const modalError = document.getElementById('modal-input-error');
  if (modalError) {
    emailForm.removeChild(modalError);
  }
}