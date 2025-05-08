// login.js
const authUtil = require('../../utils/auth');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    showPasswordLogin: false, // 默认显示微信登录
    username: '',
    password: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    // 检查是否已登录
    if (authUtil.isLoggedIn()) {
      // 已登录则跳转到首页
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  },

  /**
   * 切换登录方式
   */
  switchLoginType() {
    this.setData({
      showPasswordLogin: !this.data.showPasswordLogin
    });
  },

  /**
   * 输入用户名
   */
  inputUsername(e) {
    this.setData({
      username: e.detail.value
    });
  },

  /**
   * 输入密码
   */
  inputPassword(e) {
    this.setData({
      password: e.detail.value
    });
  },

  /**
   * 处理账号密码登录
   */
  handlePasswordLogin() {
    const { username, password } = this.data;
    
    // 表单验证
    if (!username) {
      wx.showToast({
        title: '请输入用户名',
        icon: 'none'
      });
      return;
    }
    
    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return;
    }
    
    // 调用登录
    authUtil.login(username, password).then(() => {
      // 登录成功
      wx.showToast({
        title: '登录成功',
        icon: 'success',
        duration: 1500,
        success: () => {
          // 跳转到首页
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/index/index'
            });
          }, 1500);
        }
      });
    }).catch(err => {
      // 登录失败，显示错误提示
      wx.showToast({
        title: err.message || '登录失败',
        icon: 'none'
      });
    });
  },

  /**
   * 处理微信登录
   */
  handleWxLogin() {
    authUtil.wxLogin().then(() => {
      // 登录成功
      wx.showToast({
        title: '登录成功',
        icon: 'success',
        duration: 1500,
        success: () => {
          // 跳转到首页
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/index/index'
            });
          }, 1500);
        }
      });
    }).catch(err => {
      // 登录失败，可能是用户取消了授权
      console.error('微信登录失败:', err);
    });
  }
}) 