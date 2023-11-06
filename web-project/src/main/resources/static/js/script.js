let menu = document.querySelector('#menu-icon');
let navbar = document.querySelector('.navbar');

menu.onclick = () => {
    menu.classList.toggle('bx-x');
    navbar.classList.toggle('open');
}
document.getElementById("current-year").textContent = new Date().getFullYear().toString();

function updateLocalTime() {
    const localTimeElement = document.getElementById("local-time");
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    localTimeElement.textContent = `Local time : ${hours}:${minutes}:${now.getSeconds().toString().padStart(2, '0')}`;
}
setInterval(updateLocalTime, 1000);
updateLocalTime();




