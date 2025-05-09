/**
 * API服务模块
 */
const { post, get } = require('./request');

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
  getUserInfo: () => get('/api/user/info')
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
   * @param {number} id 自习室ID
   * @returns {Promise} Promise对象
   */
  getStudyRoomDetail: (id) => get(`/api/study-rooms/${id}`)
};

/**
 * 座位相关API
 */
const seatApi = {
  /**
   * 根据自习室ID获取座位列表
   * @param {number} roomId 自习室ID
   * @param {Object} params 查询参数
   * @returns {Promise} Promise对象
   */
  getSeatsByRoomId: (roomId, params) => get(`/api/study-rooms/${roomId}/seats`, params),

  /**
   * 获取座位详情
   * @param {number} id 座位ID
   * @returns {Promise} Promise对象
   */
  getSeatDetail: (id) => get(`/api/seats/${id}`)
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
   * 获取我的预约列表
   * @param {Object} params 查询参数
   * @returns {Promise} Promise对象
   */
  getMyReservations: (params) => get('/api/reservations/my', params),

  /**
   * 取消预约
   * @param {number} id 预约ID
   * @returns {Promise} Promise对象
   */
  cancelReservation: (id) => post(`/api/reservations/${id}/cancel`)
};

module.exports = {
  authApi,
  userApi,
  studyRoomApi,
  seatApi,
  reservationApi
}; 