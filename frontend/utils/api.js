/**
 * API服务模块
 */
const { post, get, put, del } = require('./request');

/**
 * 用户认证相关API
 */
const authApi = {
  /**
   * 微信登录
   * @param {Object} data 登录参数
   * @returns {Promise} Promise对象
   */
  wxLogin: (data) => post('/api/auth/wx-login', data),

  /**
   * 账号密码登录
   * @param {Object} data 登录参数
   * @returns {Promise} Promise对象
   */
  login: (data) => post('/api/auth/login', data),

  /**
   * 退出登录
   * @returns {Promise} Promise对象
   */
  logout: () => post('/api/auth/logout')
};

/**
 * 用户相关API
 */
const userApi = {
  /**
   * 获取当前用户信息
   * @returns {Promise} Promise对象
   */
  getUserInfo: () => get('/api/user/info'),

  /**
   * 获取用户违约记录
   * @param {Object} params 查询参数
   * @returns {Promise} Promise对象
   */
  getUserViolations: (params) => get('/api/user/violations', params),

  /**
   * 获取用户违约统计
   * @returns {Promise} Promise对象
   */
  getUserViolationCount: () => get('/api/user/violation-count'),

  /**
   * 获取用户偏好设置
   * @returns {Promise} Promise对象
   */
  getUserPreferences: () => get('/api/user/preferences'),

  /**
   * 更新用户偏好设置
   * @param {Object} data 偏好设置数据
   * @returns {Promise} Promise对象
   */
  updateUserPreferences: (data) => put('/api/user/preferences', data)
};

/**
 * 自习室相关API
 */
const studyRoomApi = {
  /**
   * 获取自习室列表
   * @param {Object} params 查询参数
   * @returns {Promise} Promise对象
   */
  getStudyRooms: (params) => get('/api/study-rooms', params),

  /**
   * 获取自习室详情
   * @param {string|number} id 自习室ID
   * @returns {Promise} Promise对象
   */
  getStudyRoomDetail: (id) => {
    // 确保ID以字符串形式传递到URL中，并防止JS大整数精度问题
    const stringId = String(id);
    return get(`/api/study-rooms/${stringId}`, { _t: new Date().getTime() });
  },
  
  /**
   * 创建自习室
   * @param {Object} data 自习室数据
   * @returns {Promise} Promise对象
   */
  createStudyRoom: (data) => post('/api/study-rooms', data),
  
  /**
   * 更新自习室
   * @param {string|number} id 自习室ID
   * @param {Object} data 自习室数据
   * @returns {Promise} Promise对象
   */
  updateStudyRoom: (id, data) => put(`/api/study-rooms/${String(id)}`, data),
  
  /**
   * 删除自习室
   * @param {string|number} id 自习室ID
   * @returns {Promise} Promise对象
   */
  deleteStudyRoom: (id) => del(`/api/study-rooms/${String(id)}`),
  
  /**
   * 更新自习室状态
   * @param {string|number} id 自习室ID
   * @param {number} isActive 状态值：0-关闭，1-开放
   * @returns {Promise} Promise对象
   */
  updateStatus: (id, isActive) => put(`/api/study-rooms/${String(id)}/status?isActive=${isActive}`)
};

/**
 * 座位相关API
 */
const seatApi = {
  /**
   * 根据自习室ID获取座位列表
   * @param {string|number} roomId 自习室ID
   * @param {Object} params 查询参数
   * @returns {Promise} Promise对象
   */
  getSeatsByRoomId: (roomId, params) => get(`/api/seats/study-room/${String(roomId)}`, params),

  /**
   * 获取座位详情
   * @param {string|number} id 座位ID
   * @returns {Promise} Promise对象
   */
  getSeatDetail: (id) => get(`/api/seats/${String(id)}`)
};

/**
 * 预约相关API
 */
const reservationApi = {
  /**
   * 创建预约
   * @param {Object} data 预约数据
   * @returns {Promise} Promise对象
   */
  createReservation: (data) => post('/api/reservations', data),

  /**
   * 获取预约列表（分页）
   * @param {Object} params 查询参数
   * @returns {Promise} Promise对象
   */
  getReservations: (params) => get('/api/reservations', params),

  /**
   * 获取当前有效预约
   * @returns {Promise} Promise对象
   */
  getCurrentReservations: () => get('/api/reservations/current'),

  /**
   * 取消预约
   * @param {string|number} id 预约ID
   * @returns {Promise} Promise对象
   */
  cancelReservation: (id) => post(`/api/reservations/${String(id)}/cancel`),

  /**
   * 延长预约
   * @param {string|number} id 预约ID
   * @param {string} endTimeStr 新的结束时间
   * @returns {Promise} Promise对象
   */
  extendReservation: (id, endTimeStr) => post(`/api/reservations/${String(id)}/extend?endTimeStr=${endTimeStr}`),

  /**
   * 获取预约详情
   * @param {string|number} id 预约ID
   * @returns {Promise} Promise对象
   */
  getReservationDetail: (id) => get(`/api/reservations/${String(id)}`),

  /**
   * 检查座位可用性
   * @param {Object} params 查询参数
   * @returns {Promise} Promise对象
   */
  checkSeatAvailability: (params) => get('/api/reservations/check-availability', params)
};

/**
 * 收藏相关API
 */
const favoriteApi = {
  /**
   * 添加收藏
   * @param {Object} data 收藏数据
   * @returns {Promise} Promise对象
   */
  addFavorite: (data) => post('/api/favorites', data),
  
  /**
   * 获取我的收藏列表
   * @returns {Promise} Promise对象
   */
  getMyFavorites: () => get('/api/favorites/my'),
  
  /**
   * 删除收藏
   * @param {string|number} id 收藏ID
   * @returns {Promise} Promise对象
   */
  deleteFavorite: (id) => del(`/api/favorites/${String(id)}`)
};

/**
 * 管理员相关API
 */
const adminApi = {
  /**
   * 获取管理员列表
   * @returns {Promise} Promise对象
   */
  getAdminList: () => get('/api/admins')
};

module.exports = {
  authApi,
  userApi,
  studyRoomApi,
  seatApi,
  reservationApi,
  favoriteApi,
  adminApi
}; 