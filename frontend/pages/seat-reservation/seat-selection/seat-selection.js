const app = getApp();
const api = require('../../../utils/api');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    studyRoomId: null,
    studyRoomName: '',
    seats: [],
    maxRow: 0,
    maxColumn: 0,
    loading: true,
    selectedSeatId: null,
    selectedSeat: null,
    seatFilters: {
      hasPower: null,
      isWindow: null,
      isCorner: null,
      isAvailable: true
    },
    currentPage: 1,
    pageSize: 100,
    totalPages: 0,
    totalSeats: 0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if (options && options.studyRoomId) {
      const studyRoomId = options.studyRoomId;
      const studyRoomName = options.studyRoomName || '自习室';
      
      this.setData({
        studyRoomId,
        studyRoomName
      });
      
      wx.setNavigationBarTitle({
        title: studyRoomName + ' - 座位选择'
      });
      
      this.loadSeats();
    } else {
      wx.showToast({
        title: '参数错误',
        icon: 'error',
        duration: 2000
      });
      
      setTimeout(() => {
        wx.navigateBack();
      }, 2000);
    }
  },

  /**
   * 加载座位数据
   */
  loadSeats: function () {
    this.setData({ loading: true });
    
    const query = {
      studyRoomId: this.data.studyRoomId,
      ...this.data.seatFilters
    };
    
    api.post('/api/seats/query', query, {
      current: this.data.currentPage,
      size: this.data.pageSize
    }).then(res => {
      if (res.success) {
        const seats = res.data.records || [];
        
        // 计算最大行列数
        let maxRow = 0;
        let maxColumn = 0;
        seats.forEach(seat => {
          maxRow = Math.max(maxRow, seat.rowNumber);
          maxColumn = Math.max(maxColumn, seat.columnNumber);
        });
        
        this.setData({
          seats,
          maxRow,
          maxColumn,
          totalPages: res.data.pages || 0,
          totalSeats: res.data.total || 0,
          loading: false
        });
      } else {
        wx.showToast({
          title: res.message || '加载座位失败',
          icon: 'none',
          duration: 2000
        });
        this.setData({ loading: false });
      }
    }).catch(err => {
      console.error('加载座位异常', err);
      wx.showToast({
        title: '加载座位异常',
        icon: 'none',
        duration: 2000
      });
      this.setData({ loading: false });
    });
  },

  /**
   * 座位选择事件
   */
  onSeatSelect: function (e) {
    const seat = e.detail.seat;
    
    this.setData({
      selectedSeatId: seat.id,
      selectedSeat: seat
    });
    
    // 显示座位详情
    wx.showModal({
      title: '座位详情',
      content: `座位编号: ${seat.seatNumber}\n` +
               `位置: 第${seat.rowNumber}行 第${seat.columnNumber}列\n` + 
               `电源: ${seat.hasPowerText}\n` +
               `靠窗: ${seat.isWindowText}\n` +
               `角落: ${seat.isCornerText}`,
      confirmText: '选择此座位',
      cancelText: '取消选择',
      success: (res) => {
        if (res.confirm) {
          // 选择此座位，跳转到预约页面
          wx.navigateTo({
            url: `/pages/seat-reservation/reservation/reservation?seatId=${seat.id}&studyRoomId=${this.data.studyRoomId}&studyRoomName=${this.data.studyRoomName}&seatNumber=${seat.seatNumber}`
          });
        } else {
          // 取消选择
          this.setData({
            selectedSeatId: null,
            selectedSeat: null
          });
        }
      }
    });
  },

  /**
   * 筛选座位
   */
  toggleFilter: function (e) {
    const { type, value } = e.currentTarget.dataset;
    
    if (type && value !== undefined) {
      const seatFilters = { ...this.data.seatFilters };
      
      // 如果当前值等于点击的值，则取消筛选（设为null）
      if (seatFilters[type] === parseInt(value)) {
        seatFilters[type] = null;
      } else {
        seatFilters[type] = parseInt(value);
      }
      
      this.setData({
        seatFilters,
        currentPage: 1 // 重置到第一页
      });
      
      this.loadSeats();
    }
  },

  /**
   * 切换是否只显示可用座位
   */
  toggleAvailable: function () {
    const seatFilters = { ...this.data.seatFilters };
    seatFilters.isAvailable = !seatFilters.isAvailable;
    
    this.setData({
      seatFilters,
      currentPage: 1 // 重置到第一页
    });
    
    this.loadSeats();
  },

  /**
   * 重置筛选条件
   */
  resetFilters: function () {
    this.setData({
      seatFilters: {
        hasPower: null,
        isWindow: null,
        isCorner: null,
        isAvailable: true
      },
      currentPage: 1 // 重置到第一页
    });
    
    this.loadSeats();
  }
}) 