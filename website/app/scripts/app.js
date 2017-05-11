
(function(document) {
  'use strict';

  var app = document.querySelector('#app');

  app.displayInstalledToast = function() {
    // Check to make sure caching is actually enabled—it won't be in the dev environment.
    if (!document.querySelector('platinum-sw-cache').disabled) {
      document.querySelector('#caching-complete').show();
    }
  };

  // Toggle Drawer
  app.toggleDrawer = function() {
    var drawerPanel = document.getElementById('paperDrawerPanel');
    if (drawerPanel.narrow) {
      drawerPanel.togglePanel();
    } else {
      drawerPanel.classList.toggle('collapsed-menu');
    }
  };

  // Scroll page to top and expand header
  app.scrollPageToTop = function() {
    document.getElementById('mainContainer').scrollTop = 0;
  };

  // Initial widgets cols number
  app.cols = '2';

  // Firebase location
  app.location = 'https://sams-5a2f3.firebaseio.com';

  // Sign out user
  app.showError = function(e) {
    this.error = e.detail;
  };

  app.signInAnonymously = function() {
    this.error = null;
    this.$.auth.signInAnonymously();
  };
  
  app.signInWithGoogle = function() {
    this.error = null;
    this.$.auth.signInWithPopup();
  };

  app.signInWithEmailAndPassword = function() {
    this.error = null;
    this.$.firebase.auth().signInWithEmailAndPassword(this.email, this.password);
    this.email = null;
    this.password = null;
  };

  app.createUserWithEmailAndPassword = function() {
    this.error = null;
    this.$.auth.createUserWithEmailAndPassword(this.email, this.password);
    this.email = null;
    this.password = null;
  };

  app.signOut = function() {
    this.error = null;
    this.$.auth.signOut();
  };
})(document);
