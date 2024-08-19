// 서비스 워커에 Firebase Messaging 접근 권한을 부여합니다.
// 여기서는 Firebase Messaging만 사용할 수 있습니다.
// 다른 Firebase 라이브러리는 서비스 워커에서 사용할 수 없습니다.
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-messaging.js');

// Firebase 구성 객체를 전달하여 서비스 워커에서 Firebase 앱을 초기화합니다.
// https://firebase.google.com/docs/web/setup#config-object
const firebaseConfig = firebase.initializeApp({
  apiKey: "AIzaSyB5jI4VASgpwYPLRZ26qLoeS6aYGV3Nhh4",
  authDomain: "footfix-f417c.firebaseapp.com",
  databaseURL: 'https://footfix-f417c-default-rtdb.firebaseio.com/',
  projectId: "footfix-f417c",
  storageBucket: "footfix-f417c.appspot.com",
  messagingSenderId: "362737595673",
  appId: "1:362737595673:web:9f2e44f894f7556a570188",
  measurementId: "G-YPQCLQW43Y"
});

// 백그라운드 메시지를 처리할 수 있도록 Firebase Messaging 인스턴스를 가져옵니다.
const messaging = firebase.messaging();

self.addEventListener('push', event => {
  const notificationData = event.data.json();
  const data = notificationData.data;
  console.log('서비스워커가 받은 data:', data); // 디버깅을 위해 추가

  const options = {
    body: data.body,
    icon: data.icon,
    badge: data.badge,
    image: data.image,
  };

  event.waitUntil(
    self.clients.matchAll({ type: 'window', includeUncontrolled: true }).then(clients => {
      let isForeground = false;

      for (let i = 0; i < clients.length; i++) {
        const client = clients[i];
        if (client.visibilityState === 'visible') {
          isForeground = true;
          break;
        }
      }

      // 포어그라운드 상태일 때만 알림 표시
      if (isForeground) {
        self.registration.showNotification(data.title, options);
      }
    })
  );

});




