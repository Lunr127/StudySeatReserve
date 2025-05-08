// 封装网络请求
const app = getApp();

/**
 * 封装wx.request为Promise
 * @param {Object} options 请求选项
 * @returns {Promise} Promise对象
 */
function request(options) {
  // 获取应用实例
  const baseUrl = app ? app.globalData.baseUrl : 'http://localhost:8080';
  // 获取token
  const token = app ? app.globalData.token : '';
  
  return new Promise((resolve, reject) => {
    // 拼接url
    const url = options.url.startsWith('http') ? options.url : baseUrl + options.url;
    
    // 设置请求头
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    };
    
    // 如果有token则添加到请求头
    if (token) {
      header['Authorization'] = 'Bearer ' + token;
    }
    
    wx.request({
      url: url,
      data: options.data,
      method: options.method || 'GET',
      header: header,
      success: (res) => {
        // 根据后端的响应格式可能需要调整
        if (res.statusCode === 200) {
          // 假设后端返回格式为 { code: 0, data: any, message: '' }
          if (res.data.code === 0 || res.data.code === 200) {
            resolve(res.data);
          } else {
            // 业务逻辑错误
            wx.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            });
            reject(res.data);
          }
        } else if (res.statusCode === 401) {
          // 未授权，清除本地token，跳转登录
          wx.removeStorageSync('token');
          if (app) {
            app.globalData.token = '';
          }
          wx.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none',
            complete: () => {
              // 跳转到登录页
              wx.navigateTo({
                url: '/pages/login/login',
              });
            }
          });
          reject({ message: '登录已过期，请重新登录' });
        } else {
          // 其他错误
          wx.showToast({
            title: '服务器错误: ' + res.statusCode,
            icon: 'none'
          });
          reject(res);
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
}

// 封装GET请求
const get = (url, data = {}, options = {}) => {
  return request({
    url,
    data,
    method: 'GET',
    ...options
  });
};

// 封装POST请求
const post = (url, data = {}, options = {}) => {
  return request({
    url,
    data,
    method: 'POST',
    ...options
  });
};

// 封装PUT请求
const put = (url, data = {}, options = {}) => {
  return request({
    url,
    data,
    method: 'PUT',
    ...options
  });
};

// 封装DELETE请求
const del = (url, data = {}, options = {}) => {
  return request({
    url,
    data,
    method: 'DELETE',
    ...options
  });
};

module.exports = {
  request,
  get,
  post,
  put,
  delete: del
}; 