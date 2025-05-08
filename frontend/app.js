// app.js
App({
  globalData: {
    userInfo: null,
    token: '',
    baseUrl: 'http://localhost:8080', // 后端API基础URL
  },
  
  onLaunch: function() {
    // 从本地存储获取token
    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      // 可以在这里验证token有效性
    }
  },
  
  // 登录方法
  login: function(callback) {
    const that = this;
    // 调用wx.login获取code
    wx.login({
      success: res => {
        if (res.code) {
          // 获取code后可向后端请求登录
          console.log('微信登录成功，code:', res.code);
          // 可以在这里发起后端登录请求
          // 登录成功后设置回调
          if (callback && typeof callback === 'function') {
            callback();
          }
        } else {
          console.log('登录失败：' + res.errMsg);
        }
      }
    });
  },
  
  // 检查用户是否已登录
  checkLogin: function() {
    return !!this.globalData.token;
  }
}); 