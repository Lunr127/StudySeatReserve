const app = getApp();
const { reservationApi } = require('../../../utils/api');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    id: null,
    reservation: null,
    loading: true
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if (options.id) {
      this.setData({
        id: options.id
      });
      this.loadReservationDetail(options.id);
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

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 当页面重新显示时刷新预约详情
    if (this.data.id) {
      this.loadReservationDetail(this.data.id);
    }
  },

  /**
   * 加载预约详情
   */
  loadReservationDetail: function(id) {
    this.setData({
      loading: true
    });
    
    reservationApi.getReservationDetail(id).then(res => {
      if (res.code === 200) {
        // 格式化时间字符串
        const reservation = res.data;
        reservation.formattedStartTime = this.formatDateTime(reservation.startTime);
        reservation.formattedEndTime = this.formatDateTime(reservation.endTime);
        reservation.formattedCreateTime = this.formatDateTime(reservation.createTime);
        
        // 设置状态文本和样式类
        reservation.statusText = reservation.statusText || this.getStatusText(reservation.status);
        reservation.statusClass = this.getStatusClass(reservation.status);
        
        this.setData({
          reservation: reservation,
          loading: false
        });
      } else {
        wx.showToast({
          title: res.message || '获取预约详情失败',
          icon: 'none'
        });
        this.setData({
          loading: false
        });
      }
    }).catch(err => {
      console.error('获取预约详情错误:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
      this.setData({
        loading: false
      });
    });
  },

  /**
   * 格式化时间
   */
  formatDateTime: function(dateTimeStr) {
    if (!dateTimeStr) return '';
    
    // 将 "yyyy-MM-dd HH:mm:ss" 格式转换为 iOS 兼容的 "yyyy/MM/dd HH:mm:ss" 格式
    const formattedStr = dateTimeStr.replace(/-/g, '/');
    
    const date = new Date(formattedStr);
    
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      console.error('Invalid date:', dateTimeStr);
      return dateTimeStr; // 返回原始字符串，避免显示错误
    }
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  },

  /**
   * 跳转到签到页面
   */
  goToCheckIn: function() {
    if (!this.data.reservation) return;
    
    wx.navigateTo({
      url: `/pages/check-in/check-in?reservationId=${this.data.reservation.id}`
    });
  },

  /**
   * 取消预约
   */
  cancelReservation: function() {
    if (!this.data.reservation) return;
    
    wx.showModal({
      title: '取消预约',
      content: '确定要取消该预约吗？',
      success: (res) => {
        if (res.confirm) {
          this.doCancelReservation();
        }
      }
    });
  },

  /**
   * 执行取消预约
   */
  doCancelReservation: function() {
    wx.showLoading({
      title: '取消中...',
    });
    
    reservationApi.cancelReservation(this.data.reservation.id).then(res => {
      wx.hideLoading();
      
      if (res.code === 200) {
        wx.showToast({
          title: '取消成功',
          icon: 'success'
        });
        // 重新加载预约详情
        this.loadReservationDetail(this.data.id);
      } else {
        wx.showToast({
          title: res.message || '取消失败',
          icon: 'none'
        });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('取消预约错误:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
    });
  },

  /**
   * 延长预约
   */
  extendReservation: function() {
    if (!this.data.reservation) return;
    
    // 跳转到延长预约页面
    wx.navigateTo({
      url: `/pages/user-center/extend-reservation/extend-reservation?id=${this.data.reservation.id}&endTime=${this.data.reservation.endTime}`
    });
  },

  /**
   * 返回上一页
   */
  goBack: function() {
    wx.navigateBack();
  },

  /**
   * 获取状态文本
   */
  getStatusText: function(status) {
    const statusMap = {
      0: '已取消',
      1: '待签到',
      2: '使用中',
      3: '已完成',
      4: '已违约'
    };
    
    return statusMap[status] || '未知状态';
  },

  /**
   * 获取状态样式类
   */
  getStatusClass: function(status) {
    const statusMap = {
      0: 'status-cancelled',  // 已取消
      1: 'status-pending',    // 待签到
      2: 'status-using',      // 使用中
      3: 'status-completed',  // 已完成
      4: 'status-violated'    // 已违约
    };
    
    return statusMap[status] || '';
  }
})