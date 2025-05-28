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
      try {
        // 解码URL参数
        const decodedStartTime = decodeURIComponent(options.startTime);
        const decodedEndTime = decodeURIComponent(options.endTime);
        
        console.log('接收到的时间参数:', {
          startTime: decodedStartTime,
          endTime: decodedEndTime
        });
        
        // 尝试直接将字符串转为Date对象
        const startTime = new Date(decodedStartTime);
        const endTime = new Date(decodedEndTime);
        
        console.log('解析后的日期对象:', {
          startTime: startTime.toString(),
          startTimeValid: !isNaN(startTime.getTime()),
          endTime: endTime.toString(),
          endTimeValid: !isNaN(endTime.getTime())
        });
        
        // 在设置数据之前先生成格式化的时间显示字符串
        const formattedStartTime = this.formatDateTime(startTime);
        const formattedEndTime = this.formatDateTime(endTime);
        
        console.log('格式化后的时间显示:', {
          formattedStartTime: formattedStartTime,
          formattedEndTime: formattedEndTime
        });
        
        this.setData({
          roomId: options.roomId,
          seatId: options.seatId,
          startTime: startTime,
          endTime: endTime,
          formattedStartTime: formattedStartTime,  // 添加格式化后的字符串
          formattedEndTime: formattedEndTime       // 添加格式化后的字符串
        });
        
        this.loadStudyRoomInfo();
        this.loadSeatInfo();
      } catch (error) {
        console.error('日期解析错误:', error);
        wx.showToast({
          title: '日期格式错误',
          icon: 'error'
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
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
    try {
      // 检查是否是有效日期对象
      if (date && date instanceof Date && !isNaN(date.getTime())) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      }
      
      // 如果不是日期对象，尝试将其解析为日期
      if (typeof date === 'string') {
        const parsedDate = new Date(date);
        if (!isNaN(parsedDate.getTime())) {
          const year = parsedDate.getFullYear();
          const month = String(parsedDate.getMonth() + 1).padStart(2, '0');
          const day = String(parsedDate.getDate()).padStart(2, '0');
          const hours = String(parsedDate.getHours()).padStart(2, '0');
          const minutes = String(parsedDate.getMinutes()).padStart(2, '0');
          
          return `${year}-${month}-${day} ${hours}:${minutes}`;
        }
      }
      
      console.warn('无效的日期对象:', date);
      return '无效日期';
    } catch (e) {
      console.error('格式化日期时间出错:', e, '日期:', date);
      return '日期格式错误';
    }
  },
  
  // 获取持续时间（小时）
  getDuration: function() {
    try {
      const { startTime, endTime } = this.data;
      if (!startTime || !endTime || !(startTime instanceof Date) || !(endTime instanceof Date)) {
        console.warn('无效的开始或结束时间:', startTime, endTime);
        return '0.0';
      }
      
      // 检查日期是否有效
      if (isNaN(startTime.getTime()) || isNaN(endTime.getTime())) {
        console.warn('无效的日期时间值');
        return '0.0';
      }
      
      // 精确计算小时，保留一位小数
      const durationHours = (endTime - startTime) / (1000 * 60 * 60);
      
      // 确保结果是正数
      if (durationHours <= 0) {
        console.warn('计算得到的持续时间小于或等于0:', durationHours);
        return '0.0';
      }
      
      return durationHours.toFixed(1);
    } catch (e) {
      console.error('计算持续时间出错:', e);
      return '0.0';
    }
  },
  
  // 将Date对象格式化为本地时间的ISO格式字符串
  formatDateToLocalISOString: function(date) {
    if (!date || !(date instanceof Date) || isNaN(date.getTime())) {
      console.warn('无效的日期对象:', date);
      return '';
    }
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
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
    
    // 确保开始时间至少比当前时间晚1分钟
    const now = new Date();
    const minStartTime = new Date(now.getTime() + 60000); // 当前时间+1分钟
    
    if (startTime <= now) {
      wx.showToast({
        title: '开始时间必须是未来时间',
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
    
    console.log('准备提交预约:', {
      seatId: parseInt(seatId),
      studyRoomId: parseInt(roomId),
      startTimeLocal: startTime.toString(),
      startTimeToSend: startTime.toISOString().replace('Z', ''),
      endTimeLocal: endTime.toString(),
      endTimeToSend: endTime.toISOString().replace('Z', '')
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
        // 直接格式化为本地时间字符串，保持与传入格式一致
        startTime: this.formatDateToLocalISOString(startTime),
        endTime: this.formatDateToLocalISOString(endTime)
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