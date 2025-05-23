const app = getApp();
const { userApi } = require('../../utils/api');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    userInfo: null,
    hasLogin: false,
    studyRoomList: [], // 自习室列表
    // 轮播图数据
    bannerList: [
      {
        id: 1,
        image: '/images/banner1.png',
        title: '图书馆自习室'
      },
      {
        id: 2,
        image: '/images/banner2.png',
        title: '教学楼自习室'
      }
    ],
    // 统计数据
    statistics: {
      availableRooms: 0,
      availableSeats: 0,
      reservationCount: 0
    },
    // 最近预约
    recentReservations: []
  },

  /**
   * 跳转到自习室列表
   */
  goToStudyRooms: function() {
    wx.switchTab({
      url: '/pages/study-rooms/study-rooms',
    });
  },

  /**
   * 跳转到登录页
   */
  goToLogin: function() {
    wx.navigateTo({
      url: '/pages/login/login',
    });
  },

  /**
   * 跳转到用户中心
   */
  goToUserCenter: function() {
    wx.switchTab({
      url: '/pages/user-center/user-center',
    });
  },

  /**
   * 跳转到座位预约页
   */
  goToSeatReservation: function(e) {
    const roomId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/seat-reservation/seat-selection/seat-selection?roomId=' + roomId,
    });
  },

  /**
   * 获取自习室列表
   */
  getStudyRoomList: function() {
    // 模拟数据，实际应调用后端API
    this.setData({
      studyRoomList: [
        {
          id: 1,
          name: '中心图书馆一楼自习室',
          location: '中心图书馆一楼',
          availableSeats: 45,
          totalSeats: 100,
          openTime: '08:00-22:00',
          image: '/images/library.png'
        },
        {
          id: 2,
          name: '工学院自习室',
          location: '工学院楼3楼',
          availableSeats: 30,
          totalSeats: 80,
          openTime: '08:30-21:30',
          image: '/images/classroom.png'
        }
      ],
      statistics: {
        availableRooms: 5,
        availableSeats: 120,
        reservationCount: 256
      }
    });
  },

  /**
   * 获取最近预约
   */
  getRecentReservations: function() {
    if (this.data.hasLogin) {
      // 模拟数据，实际应调用后端API
      this.setData({
        recentReservations: [
          {
            id: 1,
            roomName: '中心图书馆一楼自习室',
            seatNumber: 'A12',
            date: '2023-05-20',
            time: '14:00-16:00',
            status: '使用中'
          }
        ]
      });
    }
  },

  /**
   * 检查登录状态并获取用户信息
   */
  checkLoginStatus: function() {
    // 检查全局数据中是否有token
    const hasLogin = app.checkLogin();
    this.setData({
      hasLogin: hasLogin
    });
    
    if (hasLogin) {
      // 如果已登录，从后端获取用户信息
      this.getUserInfo();
      // 获取最近预约
      this.getRecentReservations();
    }
  },

  /**
   * 获取用户信息
   */
  getUserInfo: function() {
    userApi.getUserInfo().then(res => {
      if (res.code === 200) {
        const userInfo = res.data;
        this.setData({
          userInfo: {
            nickName: userInfo.realName || userInfo.username,
            avatarUrl: userInfo.avatar || '/images/avatar.png',
            realName: userInfo.realName,
            username: userInfo.username,
            userType: userInfo.userType,
            phone: userInfo.phone,
            email: userInfo.email
          }
        });
        
        // 更新全局用户信息
        app.globalData.userInfo = userInfo;
        wx.setStorageSync('userInfo', userInfo);
      } else {
        console.error('获取用户信息失败:', res.message);
        // 如果获取失败，使用本地缓存的用户信息
        const cachedUserInfo = wx.getStorageSync('userInfo');
        if (cachedUserInfo) {
          this.setData({
            userInfo: {
              nickName: cachedUserInfo.realName || cachedUserInfo.username,
              avatarUrl: cachedUserInfo.avatar || '/images/avatar.png',
              realName: cachedUserInfo.realName,
              username: cachedUserInfo.username,
              userType: cachedUserInfo.userType,
              phone: cachedUserInfo.phone,
              email: cachedUserInfo.email
            }
          });
        }
      }
    }).catch(err => {
      console.error('获取用户信息错误:', err);
      // 如果网络错误，使用本地缓存的用户信息
      const cachedUserInfo = wx.getStorageSync('userInfo');
      if (cachedUserInfo) {
        this.setData({
          userInfo: {
            nickName: cachedUserInfo.realName || cachedUserInfo.username,
            avatarUrl: cachedUserInfo.avatar || '/images/avatar.png',
            realName: cachedUserInfo.realName,
            username: cachedUserInfo.username,
            userType: cachedUserInfo.userType,
            phone: cachedUserInfo.phone,
            email: cachedUserInfo.email
          }
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.checkLoginStatus();
    this.getStudyRoomList();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    // 每次页面显示时更新登录状态和数据
    this.checkLoginStatus();
    this.getStudyRoomList();
    if (this.data.hasLogin) {
      this.getRecentReservations();
    }
  }
}); 