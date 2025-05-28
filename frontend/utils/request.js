/**
 * 请求封装
 */

// 接口基础路径
const baseURL = 'http://localhost:8080';

/**
 * 封装wx.request为Promise
 * @param {Object} options - 请求选项
 * @returns {Promise} - 返回Promise对象
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 获取token
    const token = wx.getStorageSync('token');
    
    // 公共参数
    const params = {
      url: baseURL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': options.contentType || 'application/json',
        ...options.header
      },
      success: (res) => {
        // 请求成功，检查状态码和业务码
        if (res.statusCode >= 200 && res.statusCode < 300) {
          // 成功
          if (res.data.code === 200) {
            resolve(res.data);
          } else {
            // 业务错误
            reject({
              code: res.data.code,
              message: res.data.message || '请求失败'
            });
            // 显示错误提示
            wx.showToast({
              title: res.data.message || '请求失败',
              icon: 'none',
              duration: 2000
            });
          }
        } else if (res.statusCode === 401) {
          // 未授权
          reject({
            code: 401,
            message: '登录已过期，请重新登录'
          });
          // 跳转到登录页
          wx.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none',
            duration: 2000
          });
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          setTimeout(() => {
            wx.redirectTo({
              url: '/pages/login/login'
            });
          }, 2000);
        } else {
          // 其他错误
          reject({
            code: res.statusCode,
            message: '请求失败'
          });
          // 显示错误提示
          wx.showToast({
            title: '请求失败，错误码: ' + res.statusCode,
            icon: 'none',
            duration: 2000
          });
        }
      },
      fail: (err) => {
        // 请求失败
        reject(err);
        wx.showToast({
          title: '网络异常',
          icon: 'none',
          duration: 2000
        });
      }
    };
    
    // 添加认证头
    if (token) {
      params.header['Authorization'] = 'Bearer ' + token;
    }
    
    // 发起请求
    wx.request(params);
  });
};

/**
 * GET请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求参数
 * @param {Object} options - 其他配置
 * @returns {Promise} - 返回Promise对象
 */
const get = (url, data = {}, options = {}) => {
  // 处理参数中的null值，避免传递"null"字符串
  const processedData = {};
  for (const key in data) {
    if (data[key] !== null && data[key] !== undefined) {
      // 如果参数值是数组，需要特殊处理
      if (Array.isArray(data[key])) {
        // 对于数组参数，需要构建查询字符串
        // 例如：status=[0,3,4] 转换为 status=0&status=3&status=4
        // 这里先标记为数组，在构建URL时处理
        processedData[key] = data[key];
      } else {
        processedData[key] = data[key];
      }
    }
  }
  
  // 构建查询字符串
  let queryString = '';
  if (Object.keys(processedData).length > 0) {
    const queryParams = [];
    for (const key in processedData) {
      const value = processedData[key];
      if (Array.isArray(value)) {
        // 数组参数：为每个值创建一个同名参数
        value.forEach(item => {
          queryParams.push(`${encodeURIComponent(key)}=${encodeURIComponent(item)}`);
        });
      } else {
        // 普通参数
        queryParams.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`);
      }
    }
    queryString = queryParams.join('&');
  }
  
  // 如果有查询字符串，附加到URL上
  const finalUrl = queryString ? `${url}?${queryString}` : url;
  
  return request({
    url: finalUrl,
    method: 'GET',
    // GET请求不需要在data中传递参数了，因为已经构建到URL中
    ...options
  });
};

/**
 * POST请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求参数
 * @param {Object} options - 其他配置
 * @returns {Promise} - 返回Promise对象
 */
const post = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  });
};

/**
 * PUT请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求参数
 * @param {Object} options - 其他配置
 * @returns {Promise} - 返回Promise对象
 */
const put = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  });
};

/**
 * DELETE请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求参数
 * @param {Object} options - 其他配置
 * @returns {Promise} - 返回Promise对象
 */
const del = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  });
};

module.exports = {
  request,
  get,
  post,
  put,
  del,
  baseURL
}; 