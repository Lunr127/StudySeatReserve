/**
 * 认证工具模块
 */
const { authApi, userApi } = require('./api');

/**
 * 检查是否已登录
 * @returns {boolean} 是否已登录
 */
const isLoggedIn = () => {
  return !!wx.getStorageSync('token');
};

/**
 * 微信登录
 * @returns {Promise} Promise对象
 */
const wxLogin = () => {
  return new Promise((resolve, reject) => {
    // 显示加载中
    wx.showLoading({
      title: '登录中...',
      mask: true
    });

    // 获取微信登录凭证
    wx.login({
      success: (res) => {
        if (res.code) {
          // 获取用户信息
          wx.getUserProfile({
            desc: '用于完善用户资料',
            success: (userInfo) => {
              // 调用后端接口，使用code换取用户信息
              authApi.wxLogin({
                code: res.code,
                userInfo: userInfo.userInfo
              }).then(result => {
                // 保存token和用户信息
                wx.setStorageSync('token', result.data.token);
                wx.setStorageSync('userInfo', result.data);
                
                // 隐藏加载提示
                wx.hideLoading();
                resolve(result.data);
              }).catch(err => {
                wx.hideLoading();
                reject(err);
              });
            },
            fail: (err) => {
              wx.hideLoading();
              wx.showToast({
                title: '获取用户信息失败',
                icon: 'none'
              });
              reject(err);
            }
          });
        } else {
          wx.hideLoading();
          wx.showToast({
            title: '微信登录失败',
            icon: 'none'
          });
          reject(new Error('微信登录失败'));
        }
      },
      fail: (err) => {
        wx.hideLoading();
        wx.showToast({
          title: '微信登录失败',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
};

/**
 * 账号密码登录
 * @param {string} username 用户名
 * @param {string} password 密码
 * @returns {Promise} Promise对象
 */
const login = (username, password) => {
  return new Promise((resolve, reject) => {
    // 显示加载中
    wx.showLoading({
      title: '登录中...',
      mask: true
    });

    // 调用登录接口
    authApi.login({
      username,
      password
    }).then(result => {
      // 保存token和用户信息
      wx.setStorageSync('token', result.data.token);
      wx.setStorageSync('userInfo', result.data);
      
      // 隐藏加载提示
      wx.hideLoading();
      resolve(result.data);
    }).catch(err => {
      wx.hideLoading();
      reject(err);
    });
  });
};

/**
 * 退出登录
 * @returns {Promise} Promise对象
 */
const logout = () => {
  return new Promise((resolve, reject) => {
    // 调用退出登录接口
    authApi.logout().then(() => {
      // 清除本地存储
      wx.removeStorageSync('token');
      wx.removeStorageSync('userInfo');
      resolve();
    }).catch(err => {
      // 即使接口失败，也清除本地存储
      wx.removeStorageSync('token');
      wx.removeStorageSync('userInfo');
      reject(err);
    });
  });
};

/**
 * 获取用户信息
 * @returns {Promise} Promise对象
 */
const getUserInfo = () => {
  return new Promise((resolve, reject) => {
    // 如果本地有缓存，直接返回
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      resolve(userInfo);
      return;
    }

    // 调用获取用户信息接口
    userApi.getUserInfo().then(result => {
      resolve(result.data);
    }).catch(err => {
      reject(err);
    });
  });
};

module.exports = {
  isLoggedIn,
  wxLogin,
  login,
  logout,
  getUserInfo
}; 