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
let emailForm;

/** @type {HTMLElement} */
let subscribeFormSubmitted;

/** @type {HTMLElement} */
let unsubscribeFormSubmitted;

/** @type {HTMLElement} */
let submitFormButton;

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

  // When users submit the form, initiate form validation.
  emailForm = document.getElementById('email-form');
  submitFormButton = document.getElementById('form-submit');
  subscribeFormSubmitted = document.getElementById('subscribe-form-submitted');
  unsubscribeFormSubmitted = document.getElementById('unsubscribe-form-submitted');
  submitFormButton.addEventListener('click', submitForm);
}

/**
 * On click of the submit button, validate form data and send data to the servlet.
 * @return {void}
 */
async function submitForm() {
  // Disable multiple submissions.
  submitFormButton.disabled = true;

  if (validateForm()) {
    emailForm.action = '/user';

    // Based on user subscribing or unsubscribing, show confirmation that form
    // was submitted.
    if (document.getElementById('subscribe').checked) {
      subscribeFormSubmitted.style.display = 'block';
    } else {
      unsubscribeFormSubmitted.style.display = 'block';
    }

    // Waits for 4000 ms so user can read confirmation and submits the form.
    setTimeout(function() {
      emailForm.submit();
    }, 4000);
  } else {
    submitFormButton.disabled = false;
  }
}

/**
 * Goes through the form elements and marks the invalid inputs.
 * Returns whether all the inputs are valid.
 * @return {boolean}
 */
function validateForm() {
  const invalidIds = [];
  const errorMessages = [];
  const formElements = emailForm.elements;

  disableInjection(formElements);
  validateFormText(invalidIds, errorMessages, formElements);
  validateFormEmail(invalidIds, errorMessages, formElements);
  validateFormCollege(invalidIds, errorMessages, formElements);
  validateFormRadioButtons(invalidIds, errorMessages, formElements);
  markInvalidInputs(invalidIds, errorMessages, formElements);

  return (invalidIds.length === 0);
}

/**
 * Checks if the email is valid.
 * @param {array} invalidIds
 * @param {array} errorMessages
 * @param {array} formElements
 * @return {void}
 */
function validateFormEmail(invalidIds, errorMessages, formElements) {
  const email = formElements.namedItem('email').value;
  var emailPattern = /^[a-zA-Z0-9._-]+@google.com$/;
  if (!emailPattern.test(email)) {
    invalidIds.push('email');
    errorMessages.push('email must follow @google.com');
  }
}

/**
 * Checks if at least one radio button has been checked.
 * @param {array} invalidIds
 * @param {array} errorMessages
 * @param {array} formElements
 * @return {void}
 */
function validateFormRadioButtons(invalidIds, errorMessages, formElements) {
  const subscribe = formElements.namedItem('email-notif')[0].checked;
  const unsubscribe = formElements.namedItem('email-notif')[1].checked;

  if (!subscribe && !unsubscribe ) {
    invalidIds.push('subscribe');
    errorMessages.push('must pick to subscribe or unsubscribe');
  }
}

/**
 * Check if form text elements have been filled out completely.
 * @param {array} invalidIds
 * @param {array} errorMessages
 * @param {array} formElements
 * @return {void}
 */
function validateFormText(invalidIds, errorMessages, formElements) {

  // Checks if any text elements are blank.
  for (let i = 0; i < formElements.length; i++) {
    if (formElements[i].type === 'text' || formElements[i].type === 'email') {
      if (!formElements[i].value.trim()) {
        invalidIds.push(formElements[i].id);
        const errorMessage = formElements[i].name + ' is blank';
        errorMessages.push(errorMessage);
      }
    }
  }
}

/**
 * Check if valid college.
 * @param {array} invalidIds
 * @param {array} errorMessages
 * @param {array} formElements
 * @return {void}
 */
function validateFormCollege(invalidIds, errorMessages, formElements) {
  const collegeName = document.getElementById('colleges-input-form').value;
  const option = document.querySelector(`#colleges option[value='${collegeName}']`);

  if (option) {
    const collegeId = option.dataset.value;
    const selectedCollege = document.getElementById('cID');
    selectedCollege.value = collegeId;
  } else {
    invalidIds.push(formElements.namedItem('colleges').id);
    const errorMessage = 'we currently do not support that college; ' +
     'please pick college from given list';
    errorMessages.push(errorMessage);
  }
}

/**
 * Highlight invalid inputs with a red background and 
 * adds error messages.
 * @param {array} invalidIds
 * @param {array} errorMessages
 * @param {array} formElements
 * @return {void}
 */
function markInvalidInputs(invalidIds, errorMessages, formElements) {
  resetMarks(formElements);
  invalidIds.forEach((id) => {
    const element = formElements.namedItem(id);
    if (element) {
      element.style.background = '#ff999966';
    }
  });
  if (errorMessages.length > 0) {
    const formError = document.createElement('div');
    formError.setAttribute('id', 'form-input-error');
    formError.innerHTML += '<ul>';
    errorMessages.forEach((message) => {
      formError.innerHTML += '<li>' + message + '</li>';
    });
    formError.innerHTML += '</ul>';
    emailForm.insertBefore(formError, submitFormButton);
  }
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
 * Resets all the input backgrounds and gets rid of previous error text.
 * @param {array} formElements
 * @return {void}
 */
function resetMarks(formElements) {
  for (let i = 0; i < formElements.length - 1; i++) {
    const element = formElements[i];
    element.style.background = '#1b18181a';
  }
  const formError = document.getElementById('form-input-error');
  if (formError) {
    emailForm.removeChild(formError);
  }
}
