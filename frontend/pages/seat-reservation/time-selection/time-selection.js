// time-selection.js
const app = getApp();

Page({
  data: {
    roomId: null,
    seatId: null,
    studyRoom: {},
    seat: {},
    existingReservations: [],
    availableSlots: [],
    selectedStartTime: null,
    selectedEndTime: null,
    dateString: '',
    loading: true,
    rules: {
      minDuration: 1, // 最小预约时长（小时）
      maxDuration: 4, // 最大预约时长（小时）
      advanceDays: 7 // 最多可提前预约天数
    }
  },
  
  onLoad: function(options) {
    wx.showLoading({
      title: '加载中...',
    });
    
    // 获取当前日期字符串
    const today = new Date();
    const dateString = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
    
    // 获取页面参数
    if (options.roomId && options.seatId) {
      let existingReservations = [];
      try {
        existingReservations = JSON.parse(options.reservations || '[]');
      } catch (e) {
        console.error('解析预约数据失败', e);
      }
      
      this.setData({
        roomId: options.roomId,
        seatId: options.seatId,
        existingReservations: existingReservations,
        dateString: dateString
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
          // 加载可用时间段
          _this.generateAvailableTimeSlots();
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
  
  // 生成可用时间段
  generateAvailableTimeSlots: function() {
    const { studyRoom, existingReservations } = this.data;
    
    if (!studyRoom.openTime || !studyRoom.closeTime) {
      return;
    }
    
    // 从自习室开放时间生成时间段，每半小时一个时间段
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth();
    const day = today.getDate();
    
    // 解析开放和关闭时间
    const [openHour, openMinute] = studyRoom.openTime.split(':').map(Number);
    const [closeHour, closeMinute] = studyRoom.closeTime.split(':').map(Number);
    
    const slots = [];
    const now = new Date();
    now.setMinutes(now.getMinutes() + 10); // 预留10分钟的缓冲时间
    
    // 生成从开放时间到关闭时间的30分钟时间段
    let currentSlot = new Date(year, month, day, openHour, openMinute, 0);
    const endTime = new Date(year, month, day, closeHour, closeMinute, 0);
    
    while (currentSlot < endTime) {
      const slotEnd = new Date(currentSlot);
      slotEnd.setMinutes(slotEnd.getMinutes() + 30);
      
      // 检查是否与现有预约冲突
      const isConflict = existingReservations.some(reservation => {
        const reserveStart = new Date(reservation.startTime);
        const reserveEnd = new Date(reservation.endTime);
        return (currentSlot < reserveEnd && slotEnd > reserveStart);
      });
      
      // 只添加未来的时间段
      if (slotEnd > now && !isConflict) {
        slots.push({
          start: new Date(currentSlot),
          end: new Date(slotEnd),
          startTime: this.formatTime(currentSlot),
          endTime: this.formatTime(slotEnd),
          selected: false
        });
      }
      
      // 移动到下一个30分钟时间段
      currentSlot.setMinutes(currentSlot.getMinutes() + 30);
    }
    
    this.setData({
      availableSlots: slots
    });
  },
  
  // 格式化时间
  formatTime: function(date) {
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  },
  
  // 选择时间段
  selectTimeSlot: function(e) {
    const { index } = e.currentTarget.dataset;
    const { availableSlots, rules } = this.data;
    const selectedSlot = availableSlots[index];
    
    // 清除所有选择
    availableSlots.forEach(slot => {
      slot.selected = false;
    });
    
    // 标记当前选择
    selectedSlot.selected = true;
    
    // 查找可能的结束时间
    let potentialEndTime = new Date(selectedSlot.start);
    potentialEndTime.setHours(potentialEndTime.getHours() + rules.maxDuration);
    
    // 如果结束时间超过自习室关闭时间，则使用关闭时间
    const [closeHour, closeMinute] = this.data.studyRoom.closeTime.split(':').map(Number);
    const closeTime = new Date(potentialEndTime);
    closeTime.setHours(closeHour, closeMinute, 0, 0);
    
    if (potentialEndTime > closeTime) {
      potentialEndTime = closeTime;
    }
    
    // 查找最长可连续预约的结束时间
    let maxEndIndex = index;
    const startTime = selectedSlot.start;
    
    for (let i = index + 1; i < availableSlots.length; i++) {
      const currentSlot = availableSlots[i];
      
      // 如果时间段连续且不超过最大预约时长
      if (currentSlot.start.getTime() === availableSlots[i-1].end.getTime() && 
          currentSlot.end <= potentialEndTime) {
        maxEndIndex = i;
      } else {
        break;
      }
    }
    
    // 标记为结束时间
    const endSlot = availableSlots[maxEndIndex];
    
    this.setData({
      availableSlots,
      selectedStartTime: selectedSlot.start,
      selectedEndTime: endSlot.end
    });
  },
  
  // 提交预约
  submitReservation: function() {
    const { roomId, seatId, selectedStartTime, selectedEndTime } = this.data;
    
    if (!selectedStartTime || !selectedEndTime) {
      wx.showToast({
        title: '请选择时间段',
        icon: 'none'
      });
      return;
    }
    
    // 计算预约时长（小时）
    const hours = (selectedEndTime - selectedStartTime) / (1000 * 60 * 60);
    
    if (hours < this.data.rules.minDuration) {
      wx.showToast({
        title: `预约时长不能少于${this.data.rules.minDuration}小时`,
        icon: 'none'
      });
      return;
    }
    
    if (hours > this.data.rules.maxDuration) {
      wx.showToast({
        title: `预约时长不能超过${this.data.rules.maxDuration}小时`,
        icon: 'none'
      });
      return;
    }
    
    // 跳转到预约确认页面
    wx.navigateTo({
      url: `/pages/seat-reservation/confirm-reservation/confirm-reservation?roomId=${roomId}&seatId=${seatId}&startTime=${selectedStartTime.toISOString()}&endTime=${selectedEndTime.toISOString()}`
    });
  }
}) 