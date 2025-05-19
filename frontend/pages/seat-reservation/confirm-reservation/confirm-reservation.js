// confirm-reservation.js
const app = getApp();

Page({
  data: {
    roomId: null,
    seatId: null,
    startTime: null,
    endTime: null,
    studyRoom: {},
    seat: {},
    loading: true,
    submitting: false
  },
  
  onLoad: function(options) {
    wx.showLoading({
      title: '加载中...',
    });
    
    // 获取页面参数
    if (options.roomId && options.seatId && options.startTime && options.endTime) {
      this.setData({
        roomId: options.roomId,
        seatId: options.seatId,
        startTime: new Date(options.startTime),
        endTime: new Date(options.endTime)
      });
      
      this.loadStudyRoomInfo();
      this.loadSeatInfo();
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
  
  // 加载座位信息
  loadSeatInfo: function() {
    const _this = this;
    wx.request({
      url: `${app.globalData.baseUrl}/api/seats/${this.data.seatId}`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success(res) {
        wx.hideLoading();
        if (res.data.code === 200) {
          _this.setData({
            seat: res.data.data,
            loading: false
          });
        } else {
          wx.showToast({
            title: res.data.message || '获取座位信息失败',
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
  
  // 格式化时间
  formatDateTime: function(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  },
  
  // 获取持续时间（小时）
  getDuration: function() {
    const { startTime, endTime } = this.data;
    if (!startTime || !endTime) return 0;
    
    return Math.round((endTime - startTime) / (1000 * 60 * 60) * 10) / 10;
  },
  
  // 提交预约
  submitReservation: function() {
    if (this.data.submitting) return;
    
    const { roomId, seatId, startTime, endTime } = this.data;
    
    if (!seatId || !startTime || !endTime) {
      wx.showToast({
        title: '预约信息不完整',
        icon: 'none'
      });
      return;
    }
    
    this.setData({
      submitting: true
    });
    
    wx.showLoading({
      title: '提交中...',
    });
    
    wx.request({
      url: `${app.globalData.baseUrl}/api/reservations`,
      method: 'POST',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`,
        'Content-Type': 'application/json'
      },
      data: {
        seatId: parseInt(seatId),
        studyRoomId: parseInt(roomId),
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString()
      },
      success: (res) => {
        wx.hideLoading();
        
        if (res.data.code === 200) {
          wx.showToast({
            title: '预约成功',
            icon: 'success'
          });
          
          // 延迟跳转到预约详情页
          setTimeout(() => {
            wx.redirectTo({
              url: `/pages/user-center/my-reservations/my-reservations`
            });
          }, 1500);
        } else {
          wx.showToast({
            title: res.data.message || '预约失败',
            icon: 'none'
          });
          this.setData({
            submitting: false
          });
        }
      },
      fail: () => {
        wx.hideLoading();
        wx.showToast({
          title: '网络错误',
          icon: 'error'
        });
        this.setData({
          submitting: false
        });
      }
    });
  },
  
  // 返回上一页
  goBack: function() {
    wx.navigateBack();
  }
}) 