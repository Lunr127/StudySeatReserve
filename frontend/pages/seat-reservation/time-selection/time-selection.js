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
    selectedDate: null, // 添加选择的日期
    dates: [], // 添加日期选择列表
    selectedDateIndex: 0, // 选中日期索引
    startTimeValue: '', // 开始时间
    endTimeValue: '', // 结束时间
    loading: true,
    rules: {
      minDuration: 1, // 最小预约时长（小时）
      maxDuration: 24, // 最大预约时长不设上限（设置为24小时）
      advanceDays: 7 // 最多可提前预约天数
    },
    // 动态计算的时间范围
    startTimeMin: '',
    startTimeMax: '',
    endTimeMin: '',
    endTimeMax: ''
  },
  
  onLoad: function(options) {
    wx.showLoading({
      title: '加载中...',
    });
    
    // 生成可选择的日期列表（当前日期及未来6天）
    const dates = [];
    for (let i = 0; i < this.data.rules.advanceDays; i++) {
      const date = new Date();
      date.setDate(date.getDate() + i);
      dates.push({
        date: new Date(date),
        dateString: this.formatDate(date)
      });
    }
    
    // 获取当前日期字符串
    const today = new Date();
    const dateString = this.formatDate(today);
    
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
        dateString: dateString,
        selectedDate: today,
        dates: dates
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
          const studyRoom = res.data.data;
          
          // 格式化开放时间显示
          if (studyRoom.openTime) {
            if (typeof studyRoom.openTime === 'string') {
              studyRoom.openTime = studyRoom.openTime.substring(0, 5);
            } else if (studyRoom.openTime.toString) {
              // 将逗号格式转换为冒号格式
              const openTimeStr = studyRoom.openTime.toString();
              if (openTimeStr.includes(',')) {
                const parts = openTimeStr.split(',').map(Number);
                studyRoom.openTime = `${String(parts[0]).padStart(2, '0')}:${String(parts[1]).padStart(2, '0')}`;
              } else {
                studyRoom.openTime = openTimeStr.substring(0, 5);
              }
            }
          }
          
          if (studyRoom.closeTime) {
            if (typeof studyRoom.closeTime === 'string') {
              studyRoom.closeTime = studyRoom.closeTime.substring(0, 5);
            } else if (studyRoom.closeTime.toString) {
              // 将逗号格式转换为冒号格式
              const closeTimeStr = studyRoom.closeTime.toString();
              if (closeTimeStr.includes(',')) {
                const parts = closeTimeStr.split(',').map(Number);
                studyRoom.closeTime = `${String(parts[0]).padStart(2, '0')}:${String(parts[1]).padStart(2, '0')}`;
              } else {
                studyRoom.closeTime = closeTimeStr.substring(0, 5);
              }
            }
          }
          
          _this.setData({
            studyRoom: studyRoom
          });
          
          // 初始化时间选择器的值
          _this.initTimeSelectors();
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
    
    // 解析开放和关闭时间 - 处理不同格式的情况
    let openHour, openMinute, closeHour, closeMinute;
    
    try {
      // 如果是字符串格式 (如 "08:00")
      if (typeof studyRoom.openTime === 'string') {
        [openHour, openMinute] = studyRoom.openTime.split(':').map(Number);
      } 
      // 如果是对象格式 (如 {hour: 8, minute: 0})
      else if (typeof studyRoom.openTime === 'object') {
        openHour = studyRoom.openTime.hour || 0;
        openMinute = studyRoom.openTime.minute || 0;
      } else {
        console.error('无法识别的openTime格式:', studyRoom.openTime);
        openHour = 8;  // 默认值
        openMinute = 0;
      }
      
      // 同样处理closeTime
      if (typeof studyRoom.closeTime === 'string') {
        [closeHour, closeMinute] = studyRoom.closeTime.split(':').map(Number);
      } 
      else if (typeof studyRoom.closeTime === 'object') {
        closeHour = studyRoom.closeTime.hour || 18;
        closeMinute = studyRoom.closeTime.minute || 0;
      } else {
        console.error('无法识别的closeTime格式:', studyRoom.closeTime);
        closeHour = 18;  // 默认值
        closeMinute = 0;
      }
    } catch (e) {
      console.error('解析时间出错:', e);
      // 使用默认值
      openHour = 8;
      openMinute = 0;
      closeHour = 18;
      closeMinute = 0;
    }
    
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
  
  // 格式化日期
  formatDate: function(date) {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  },
  
  // 格式化时间
  formatTime: function(date) {
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  },
  
  // 初始化时间选择器
  initTimeSelectors: function() {
    const { studyRoom, selectedDate } = this.data;
    
    if (!studyRoom.openTime || !studyRoom.closeTime) {
      return;
    }
    
    // 获取开放时间和关闭时间
    let openTime = studyRoom.openTime;
    let closeTime = studyRoom.closeTime;
    
    // 检查开放时间和关闭时间格式
    if (!openTime.includes(':')) {
      openTime = '08:00'; // 默认值
    }
    
    if (!closeTime.includes(':')) {
      closeTime = '22:00'; // 默认值
    }
    
    // 如果是当天，则开始时间默认为当前时间+1分钟
    const today = new Date();
    const isToday = selectedDate && 
                    today.getFullYear() === selectedDate.getFullYear() &&
                    today.getMonth() === selectedDate.getMonth() &&
                    today.getDate() === selectedDate.getDate();
    
    if (isToday) {
      // 获取当前时间并+1分钟
      const now = new Date();
      now.setMinutes(now.getMinutes() + 1); // 当前时间+1分钟
      
      const currentHour = now.getHours();
      const currentMinute = now.getMinutes();
      
      // 格式化当前时间+1分钟
      const currentTime = `${String(currentHour).padStart(2, '0')}:${String(currentMinute).padStart(2, '0')}`;
      
      // 解析开放时间
      const [openHour, openMinute] = openTime.split(':').map(Number);
      
      // 转换为分钟进行比较
      const openTotalMinutes = openHour * 60 + openMinute;
      const currentTotalMinutes = currentHour * 60 + currentMinute;
      
      // 使用较晚的那个作为开始时间
      if (currentTotalMinutes > openTotalMinutes) {
        openTime = currentTime;
      }
      
      // 设置结束时间为开始时间后2小时，但不超过关闭时间
      const [closeHour, closeMinute] = closeTime.split(':').map(Number);
      const closeTotalMinutes = closeHour * 60 + closeMinute;
      
      // 计算开始时间后2小时
      let endTotalMinutes = Math.min(currentTotalMinutes + 120, closeTotalMinutes);
      const endHour = Math.floor(endTotalMinutes / 60);
      const endMinute = endTotalMinutes % 60;
      
      const suggestedEndTime = `${String(endHour).padStart(2, '0')}:${String(endMinute).padStart(2, '0')}`;
      
      // 更新结束时间，但确保不超过自习室关闭时间
      closeTime = endTotalMinutes <= closeTotalMinutes ? suggestedEndTime : closeTime;
    } else {
      // 非当天的情况下，开始时间为开放时间，结束时间为开放时间+2小时或关闭时间
      const [openHour, openMinute] = openTime.split(':').map(Number);
      const [closeHour, closeMinute] = closeTime.split(':').map(Number);
      
      const openTotalMinutes = openHour * 60 + openMinute;
      const closeTotalMinutes = closeHour * 60 + closeMinute;
      
      // 默认预约2小时
      let endTotalMinutes = Math.min(openTotalMinutes + 120, closeTotalMinutes);
      const endHour = Math.floor(endTotalMinutes / 60);
      const endMinute = endTotalMinutes % 60;
      
      closeTime = `${String(endHour).padStart(2, '0')}:${String(endMinute).padStart(2, '0')}`;
    }
    
    this.setData({
      startTimeValue: openTime,
      endTimeValue: closeTime,
      loading: false
    });
    
    // 更新时间范围
    this.updateTimeRanges();
  },
  
  // 更新时间选择器的范围
  updateTimeRanges: function() {
    const { studyRoom, selectedDateIndex, startTimeValue } = this.data;
    
    let startTimeMin = '';
    let startTimeMax = studyRoom.closeTime || '23:59';
    let endTimeMin = '';
    let endTimeMax = studyRoom.closeTime || '23:59';
    
    // 如果是当天，最小时间为当前时间+1分钟
    if (selectedDateIndex === 0) {
      const now = new Date();
      now.setMinutes(now.getMinutes() + 1);
      startTimeMin = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
      endTimeMin = startTimeMin;
    } else {
      // 非当天，最小时间为开放时间
      startTimeMin = studyRoom.openTime || '00:00';
      endTimeMin = studyRoom.openTime || '00:00';
    }
    
    // 结束时间的最小值为开始时间（如果已选择）
    if (startTimeValue) {
      endTimeMin = startTimeValue;
    }
    
    this.setData({
      startTimeMin,
      startTimeMax,
      endTimeMin,
      endTimeMax
    });
  },
  
  // 计算预约时长
  calculateDuration: function(startTime, endTime) {
    console.log('计算预约时长:', startTime, endTime); // 调试用
    
    if (!startTime || !endTime) {
      console.log('缺少开始或结束时间');
      return '0.0';
    }
    
    try {
      const [startHour, startMinute] = startTime.split(':').map(Number);
      const [endHour, endMinute] = endTime.split(':').map(Number);
      
      // 验证解析出的是合法数值
      if (isNaN(startHour) || isNaN(startMinute) || isNaN(endHour) || isNaN(endMinute)) {
        console.log('时间格式不正确');
        return '0.0';
      }
      
      // 计算总分钟数
      const startMinutes = startHour * 60 + startMinute;
      const endMinutes = endHour * 60 + endMinute;
      
      // 如果结束时间小于开始时间，表示跨天，这里暂不处理跨天情况
      if (endMinutes <= startMinutes) {
        console.log('结束时间不能早于开始时间');
        return '0.0';
      }
      
      // 计算小时，保留一位小数
      const hours = (endMinutes - startMinutes) / 60;
      console.log('计算结果:', hours);
      return hours.toFixed(1);
    } catch (e) {
      console.error('计算预约时长出错:', e);
      return '0.0';
    }
  },
  
  // 选择日期
  bindDateChange: function(e) {
    const index = Number(e.detail.value);
    const selectedDate = this.data.dates[index].date;
    
    // 检查日期是否实际更改
    if (this.data.selectedDateIndex === index) {
      return; // 没有变化，直接返回
    }
    
    this.setData({
      selectedDateIndex: index,
      selectedDate: selectedDate,
      dateString: this.data.dates[index].dateString
    }, () => {
      // 日期改变时，重新初始化时间选择器，但先等待数据更新完成
      setTimeout(() => {
        this.initTimeSelectors();
      }, 50);
    });
    
    // 更新时间范围
    this.updateTimeRanges();
  },
  
  // 选择开始时间
  bindStartTimeChange: function(e) {
    const newStartTime = e.detail.value;
    
    // 获取当前的结束时间
    let { endTimeValue, studyRoom, selectedDateIndex } = this.data;
    
    // 检查开始时间是否在允许的时间范围内
    const [closeHour, closeMinute] = (studyRoom.closeTime || '22:00').split(':').map(Number);
    const [startHour, startMinute] = newStartTime.split(':').map(Number);
    
    const closeTotalMinutes = closeHour * 60 + closeMinute;
    const startTotalMinutes = startHour * 60 + startMinute;
    
    // 如果是当天，检查是否早于当前时间+1分钟
    const today = new Date();
    const isToday = selectedDateIndex === 0;
    
    if (isToday) {
      const now = new Date();
      now.setMinutes(now.getMinutes() + 1); // 当前时间+1分钟
      const currentTotalMinutes = now.getHours() * 60 + now.getMinutes();
      
      if (startTotalMinutes < currentTotalMinutes) {
        wx.showToast({
          title: '开始时间不能早于当前时间',
          icon: 'none'
        });
        return;
      }
    } else {
      // 非当天，检查是否早于开放时间
      const [openHour, openMinute] = (studyRoom.openTime || '08:00').split(':').map(Number);
      const openTotalMinutes = openHour * 60 + openMinute;
      
      if (startTotalMinutes < openTotalMinutes) {
        wx.showToast({
          title: `开始时间不能早于${studyRoom.openTime}`,
          icon: 'none'
        });
        return;
      }
    }
    
    // 检查是否晚于关闭时间
    if (startTotalMinutes >= closeTotalMinutes) {
      wx.showToast({
        title: `开始时间不能晚于${studyRoom.closeTime}`,
        icon: 'none'
      });
      return;
    }
    
    // 自动设置结束时间为开始时间+2小时
    let newEndMinutes = startTotalMinutes + 120; // +2小时
    
    // 确保不超过关闭时间
    if (newEndMinutes > closeTotalMinutes) {
      newEndMinutes = closeTotalMinutes;
    }
    
    const newEndHour = Math.floor(newEndMinutes / 60);
    const newEndMinute = newEndMinutes % 60;
    
    endTimeValue = `${String(newEndHour).padStart(2, '0')}:${String(newEndMinute).padStart(2, '0')}`;
    
    this.setData({
      startTimeValue: newStartTime,
      endTimeValue: endTimeValue
    });
    
    // 更新时间范围
    this.updateTimeRanges();
  },
  
  // 选择结束时间
  bindEndTimeChange: function(e) {
    const newEndTime = e.detail.value;
    const { startTimeValue, studyRoom, selectedDateIndex } = this.data;
    
    // 获取关闭时间
    const [closeHour, closeMinute] = (studyRoom.closeTime || '22:00').split(':').map(Number);
    const closeTotalMinutes = closeHour * 60 + closeMinute;
    
    // 解析新的结束时间
    const [endHour, endMinute] = newEndTime.split(':').map(Number);
    const endTotalMinutes = endHour * 60 + endMinute;
    
    // 检查是否超过关闭时间
    if (endTotalMinutes > closeTotalMinutes) {
      wx.showToast({
        title: `结束时间不能超过${studyRoom.closeTime}`,
        icon: 'none'
      });
      return;
    }
    
    // 如果是当天，检查是否早于当前时间+1分钟
    const isToday = selectedDateIndex === 0;
    
    if (isToday) {
      const now = new Date();
      now.setMinutes(now.getMinutes() + 1); // 当前时间+1分钟
      const currentTotalMinutes = now.getHours() * 60 + now.getMinutes();
      
      if (endTotalMinutes < currentTotalMinutes) {
        wx.showToast({
          title: '结束时间不能早于当前时间',
          icon: 'none'
        });
        return;
      }
    }
    
    // 检查结束时间是否早于开始时间
    if (startTimeValue) {
      const [startHour, startMinute] = startTimeValue.split(':').map(Number);
      const startTotalMinutes = startHour * 60 + startMinute;
      
      if (endTotalMinutes <= startTotalMinutes) {
        wx.showToast({
          title: '结束时间必须晚于开始时间',
          icon: 'none'
        });
        return;
      }
    }
    
    // 验证通过，更新结束时间
    this.setData({
      endTimeValue: newEndTime
    });
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
    let closeHour, closeMinute;
    try {
      // 如果是字符串格式 (如 "18:00")
      if (typeof this.data.studyRoom.closeTime === 'string') {
        [closeHour, closeMinute] = this.data.studyRoom.closeTime.split(':').map(Number);
      } 
      // 如果是对象格式 (如 {hour: 18, minute: 0})
      else if (typeof this.data.studyRoom.closeTime === 'object') {
        closeHour = this.data.studyRoom.closeTime.hour || 18;
        closeMinute = this.data.studyRoom.closeTime.minute || 0;
      } else {
        console.error('无法识别的closeTime格式:', this.data.studyRoom.closeTime);
        closeHour = 18;
        closeMinute = 0;
      }
    } catch (e) {
      console.error('解析时间出错:', e);
      closeHour = 18;
      closeMinute = 0;
    }
    
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
    const { roomId, seatId, selectedDate, startTimeValue, endTimeValue, studyRoom } = this.data;
    
    if (!selectedDate || !startTimeValue || !endTimeValue) {
      wx.showToast({
        title: '请选择日期和时间',
        icon: 'none'
      });
      return;
    }
    
    // 验证时间格式
    if (!startTimeValue.includes(':') || !endTimeValue.includes(':')) {
      wx.showToast({
        title: '时间格式错误，请重新选择',
        icon: 'none'
      });
      return;
    }
    
    // 解析时间
    const [startHour, startMinute] = startTimeValue.split(':').map(Number);
    const [endHour, endMinute] = endTimeValue.split(':').map(Number);
    
    // 安全检查：验证时间是否合法
    if (isNaN(startHour) || isNaN(startMinute) || isNaN(endHour) || isNaN(endMinute)) {
      wx.showToast({
        title: '时间格式错误，请重新选择',
        icon: 'none'
      });
      return;
    }
    
    // 构建开始时间和结束时间
    const startTime = new Date(selectedDate);
    const endTime = new Date(selectedDate);
    
    // 安全设置时间
    try {
      startTime.setHours(startHour, startMinute, 0, 0);
      endTime.setHours(endHour, endMinute, 0, 0);
    } catch (e) {
      console.error('设置时间出错:', e);
      wx.showToast({
        title: '时间设置错误，请重新选择',
        icon: 'none'
      });
      return;
    }
    
    // 检查时间是否合法
    if (startTime >= endTime) {
      wx.showToast({
        title: '开始时间必须早于结束时间',
        icon: 'none'
      });
      return;
    }
    
    // 当前时间（只需要确保是未来时间即可）
    const now = new Date();
    now.setMinutes(now.getMinutes() - 5);

    
    // 检查是否是未来时间
    if (startTime <= now) {
      wx.showToast({
        title: '预约时间必须是未来时间',
        icon: 'none'
      });
      return;
    }
    
    // 计算预约时长（小时）
    const hours = (endTime - startTime) / (1000 * 60 * 60);
    
    if (hours < this.data.rules.minDuration) {
      wx.showToast({
        title: `预约时长不能少于${this.data.rules.minDuration}小时`,
        icon: 'none'
      });
      return;
    }
    
    // 预约时长不设上限
    // if (hours > this.data.rules.maxDuration) {
    //   wx.showToast({
    //     title: `预约时长不能超过${this.data.rules.maxDuration}小时`,
    //     icon: 'none'
    //   });
    //   return;
    // }
    
    // 确保时间可以正确序列化并传递到下一个页面
    try {
      // 直接格式化为本地时间字符串（YYYY-MM-DDTHH:MM:SS格式），不再使用toISOString
      const formatLocalTime = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
      };
      
      const startTimeLocal = formatLocalTime(startTime);
      const endTimeLocal = formatLocalTime(endTime);

      // 在跳转前打印日志，帮助调试
      console.log('提交预约时间:', {
        startTimeStr: startTimeLocal,
        endTimeStr: endTimeLocal,
        startTimeObj: startTime.toString(),
        endTimeObj: endTime.toString()
      });
      
      // 确保时间格式一致，确保日期能够正确传递 - 使用更可靠的格式
      const startTimeStr = `${startTime.getFullYear()}-${String(startTime.getMonth() + 1).padStart(2, '0')}-${String(startTime.getDate()).padStart(2, '0')}T${String(startTime.getHours()).padStart(2, '0')}:${String(startTime.getMinutes()).padStart(2, '0')}:00`;
      const endTimeStr = `${endTime.getFullYear()}-${String(endTime.getMonth() + 1).padStart(2, '0')}-${String(endTime.getDate()).padStart(2, '0')}T${String(endTime.getHours()).padStart(2, '0')}:${String(endTime.getMinutes()).padStart(2, '0')}:00`;

      console.log('最终提交的时间格式:', {
        startTime: startTimeStr,
        endTime: endTimeStr
      });
      
      // 跳转到预约确认页面 - 使用encodeURIComponent确保URL参数正确传递
      wx.navigateTo({
        url: `/pages/seat-reservation/confirm-reservation/confirm-reservation?roomId=${roomId}&seatId=${seatId}&startTime=${encodeURIComponent(startTimeStr)}&endTime=${encodeURIComponent(endTimeStr)}`
      });
    } catch (e) {
      console.error('序列化时间出错:', e);
      wx.showToast({
        title: '无效的时间值，请重新选择',
        icon: 'none'
      });
      return;
    }
  }
}) 