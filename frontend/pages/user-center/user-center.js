// user-center.js
const app = getApp();
const { userApi, reservationApi, authUtil } = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    hasLogin: false,
    // 统计数据
    statistics: {
      totalReservations: 0,
      currentReservations: 0,
      completedReservations: 0,
      violationCount: 0
    },
    // 功能菜单
    menuItems: [
      {
        id: 'my-reservations',
        icon: '/images/reservation.png',
        title: '我的预约',
        subtitle: '查看预约记录',
        url: '/pages/user-center/my-reservations/my-reservations'
      },
      {
        id: 'my-favorites',
        icon: '/images/favorite.png',
        title: '我的收藏',
        subtitle: '收藏的自习室',
        url: '/pages/user-center/my-favorites/my-favorites'
      },
      {
        id: 'violation-records',
        icon: '/images/violation.png',
        title: '违约记录',
        subtitle: '查看违约情况',
        url: '/pages/user-center/violation-records/violation-records'
      },
      {
        id: 'settings',
        icon: '/images/settings.png',
        title: '个人设置',
        subtitle: '账号与偏好设置',
        url: '/pages/user-center/settings/settings'
      }
    ]
  },
  
  /**
   * 页面加载
   */
  onLoad: function(options) {
    this.checkLoginStatus();
  },

  /**
   * 页面显示
   */
  onShow: function() {
    this.checkLoginStatus();
    if (this.data.hasLogin) {
      this.loadStatistics();
    }
  },

  /**
   * 检查登录状态
   */
  checkLoginStatus: function() {
    const hasLogin = app.checkLogin();
    this.setData({
      hasLogin: hasLogin
    });
    
    if (hasLogin) {
      this.getUserInfo();
    }
  },

  /**
   * 获取用户信息
   */
  getUserInfo: function() {
    // 优先使用全局数据
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo
      });
      return;
    }

    // 如果没有全局数据，从后端获取
    userApi.getUserInfo().then(res => {
      if (res.code === 200) {
        const userInfo = res.data;
        this.setData({
          userInfo: userInfo
        });
        
        // 更新全局用户信息
        app.globalData.userInfo = userInfo;
        wx.setStorageSync('userInfo', userInfo);
      } else {
        console.error('获取用户信息失败:', res.message);
        wx.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        });
      }
    }).catch(err => {
      console.error('获取用户信息错误:', err);
      // 尝试使用本地缓存
      const cachedUserInfo = wx.getStorageSync('userInfo');
      if (cachedUserInfo) {
        this.setData({
          userInfo: cachedUserInfo
        });
      }
    });
  },

  /**
   * 加载统计数据
   */
  loadStatistics: function() {
    // 获取当前预约数量
    reservationApi.getCurrentReservations().then(res => {
      if (res.code === 200) {
        this.setData({
          'statistics.currentReservations': res.data.length
        });
      }
    }).catch(err => {
      console.error('获取当前预约失败:', err);
    });

    // 获取总预约数量（通过分页接口获取总数）
    reservationApi.getReservations({
      current: 1,
      size: 1
    }).then(res => {
      if (res.code === 200) {
        this.setData({
          'statistics.totalReservations': res.data.total || 0
        });
      }
    }).catch(err => {
      console.error('获取预约统计失败:', err);
    });

    // 获取已完成预约数量
    reservationApi.getReservations({
      current: 1,
      size: 1,
      status: 3 // 已完成状态
    }).then(res => {
      if (res.code === 200) {
        this.setData({
          'statistics.completedReservations': res.data.total || 0
        });
      }
    }).catch(err => {
      console.error('获取已完成预约失败:', err);
    });
  },

  /**
   * 点击菜单项
   */
  onMenuItemTap: function(e) {
    const item = e.currentTarget.dataset.item;
    
    if (!this.data.hasLogin) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    if (item.url) {
      wx.navigateTo({
        url: item.url
      });
    }
  },

  /**
   * 跳转到登录页
   */
  goToLogin: function() {
    wx.navigateTo({
      url: '/pages/login/login'
    });
  },

  /**
   * 退出登录
   */
  logout: function() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout(() => {
            this.setData({
              hasLogin: false,
              userInfo: null,
              statistics: {
                totalReservations: 0,
                currentReservations: 0,
                completedReservations: 0,
                violationCount: 0
              }
            });
            
            wx.showToast({
              title: '已退出登录',
              icon: 'success'
            });
          });
        }
      }
    });
  },

  /**
   * 编辑个人信息
   */
  editProfile: function() {
    wx.navigateTo({
      url: '/pages/user-center/edit-profile/edit-profile'
    });
  }
}); 