// app.js
const authUtil = require('./utils/auth');

App({
  globalData: {
    userInfo: null,
    token: '',
    baseUrl: 'http://localhost:8080', // 后端API基础URL
  },
  
  onLaunch: function() {
    // 从本地存储获取token和用户信息
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (token) {
      this.globalData.token = token;
      
      // 获取用户信息
      if (userInfo) {
        this.globalData.userInfo = userInfo;
      } else {
        // 如果本地没有用户信息但有token，则从服务器获取
        this.getUserInfo();
      }
    }
  },
  
  // 获取用户信息
  getUserInfo: function() {
    // 检查是否已登录
    if (!this.checkLogin()) {
      return;
    }
    
    // 从服务器获取用户信息
    authUtil.getUserInfo().then(userInfo => {
      // 更新全局数据
      this.globalData.userInfo = userInfo;
      // 缓存到本地
      wx.setStorageSync('userInfo', userInfo);
    }).catch(err => {
      console.error('获取用户信息失败:', err);
    });
  },
  
  // 检查用户是否已登录
  checkLogin: function() {
    return authUtil.isLoggedIn();
  },
  
  // 退出登录
  logout: function(callback) {
    authUtil.logout().then(() => {
      // 清除全局数据
      this.globalData.token = '';
      this.globalData.userInfo = null;
      
      // 执行回调
      if (callback && typeof callback === 'function') {
        callback();
      }
    }).catch(err => {
      console.error('退出登录失败:', err);
    });
  }
}); 