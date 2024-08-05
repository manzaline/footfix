document.addEventListener("DOMContentLoaded", function() {
  const signUpBtn = document.querySelector("#signUp");
  const signInBtn = document.querySelector("#signIn");
  const container = document.querySelector(".container");

  signUpBtn.addEventListener("click", () => {
    container.classList.add("right-panel-active");
  });
  signInBtn.addEventListener("click", () => {
    container.classList.remove("right-panel-active");
  });
});