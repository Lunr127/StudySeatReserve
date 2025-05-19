const app = getApp();

Page({
  /**
   * 页面的初始数据
   */
  data: {
    roomId: null,
    studyRoom: {},
    seats: [],
    selectedSeat: null,
    filters: {
      hasPower: false,
      isWindow: false,
      isCorner: false
    },
    loading: true,
    showFilterModal: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    wx.showLoading({
      title: '加载中...',
    });
    
    // 获取页面参数
    if (options.roomId) {
      this.setData({
        roomId: options.roomId
      });
      this.loadStudyRoomInfo();
      this.loadSeats();
    } else {
      wx.showToast({
        title: '参数错误',
        icon: 'error'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  // 加载自习室信息
  loadStudyRoomInfo: function() {
    const _this = this;
    wx.request({
      url: `${app.globalData.baseUrl}/api/study-rooms/${this.data.roomId}`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success(res) {
        if (res.data.code === 200) {
          _this.setData({
            studyRoom: res.data.data
          });
        } else {
          wx.showToast({
            title: res.data.message || '获取自习室信息失败',
            icon: 'none'
          });
        }
      },
      fail() {
        wx.showToast({
          title: '网络错误',
          icon: 'error'
        });
      }
    });
  },

  // 加载座位列表
  loadSeats: function() {
    const _this = this;
    const { hasPower, isWindow, isCorner } = this.data.filters;
    
    wx.request({
      url: `${app.globalData.baseUrl}/api/seats/query`,
      method: 'GET',
      data: {
        studyRoomId: this.data.roomId,
        hasPower: hasPower ? 1 : null,
        isWindow: isWindow ? 1 : null,
        isCorner: isCorner ? 1 : null,
        isAvailable: true,
        status: 1
      },
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success(res) {
        wx.hideLoading();
        if (res.data.code === 200) {
          _this.setData({
            seats: res.data.data.records,
            loading: false
          });
        } else {
          wx.showToast({
            title: res.data.message || '获取座位列表失败',
            icon: 'none'
          });
        }
      },
      fail() {
        wx.hideLoading();
        wx.showToast({
          title: '网络错误',
          icon: 'error'
        });
        _this.setData({
          loading: false
        });
      }
    });
  },

  // 选择座位
  onSeatSelect: function(e) {
    const { seat } = e.currentTarget.dataset;
    
    // 检查座位是否可用
    if (!seat.isAvailable) {
      wx.showToast({
        title: '该座位不可用',
        icon: 'none'
      });
      return;
    }
    
    this.setData({
      selectedSeat: seat
    });
    
    // 查询当天该座位的预约情况
    this.loadSeatReservations(seat.id);
  },

  // 加载座位当天预约情况
  loadSeatReservations: function(seatId) {
    const _this = this;
    wx.showLoading({
      title: '查询中...',
    });
    
    wx.request({
      url: `${app.globalData.baseUrl}/api/reservations/seat/${seatId}/today`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success(res) {
        wx.hideLoading();
        if (res.data.code === 200) {
          // 跳转到时间段选择页面
          wx.navigateTo({
            url: `/pages/seat-reservation/time-selection/time-selection?roomId=${_this.data.roomId}&seatId=${seatId}&reservations=${JSON.stringify(res.data.data)}`
          });
        } else {
          wx.showToast({
            title: res.data.message || '查询预约情况失败',
            icon: 'none'
          });
        }
      },
      fail() {
        wx.hideLoading();
        wx.showToast({
          title: '网络错误',
          icon: 'error'
        });
      }
    });
  },

  // 显示筛选弹窗
  showFilter: function() {
    this.setData({
      showFilterModal: true
    });
  },

  // 隐藏筛选弹窗
  hideFilter: function() {
    this.setData({
      showFilterModal: false
    });
  },

  // 切换筛选选项
  toggleFilter: function(e) {
    const { type } = e.currentTarget.dataset;
    const { filters } = this.data;
    
    filters[type] = !filters[type];
    
    this.setData({
      filters
    });
  },

  // 应用筛选
  applyFilter: function() {
    this.setData({
      loading: true,
      showFilterModal: false
    });
    
    wx.showLoading({
      title: '筛选中...',
    });
    
    this.loadSeats();
  },

  // 重置筛选
  resetFilter: function() {
    this.setData({
      filters: {
        hasPower: false,
        isWindow: false,
        isCorner: false
      }
    });
  }
}) 