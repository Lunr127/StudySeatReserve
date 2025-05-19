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
    loading: true,
    maxRow: 0,
    maxColumn: 0,
    // 座位查询筛选条件
    seatQuery: {
      hasPower: null,
      isWindow: null,
      isCorner: null,
      status: null
    },
    // 生成座位相关数据
    generateVisible: false,
    rows: 5,
    columns: 5,
    hasPower: 1,
    // 编辑座位相关数据
    editVisible: false,
    currentSeat: null,
    // 分页相关
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
        title: studyRoomName + ' - 座位管理'
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
      ...this.data.seatQuery
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
   * 座位点击事件
   */
  onSeatSelect: function (e) {
    const seat = e.detail.seat;
    
    this.setData({
      currentSeat: seat
    });
    
    // 显示操作菜单
    wx.showActionSheet({
      itemList: ['编辑座位', '更改状态', '删除座位'],
      success: (res) => {
        switch (res.tapIndex) {
          case 0: // 编辑座位
            this.showEditSeat();
            break;
          case 1: // 更改状态
            this.toggleSeatStatus();
            break;
          case 2: // 删除座位
            this.confirmDeleteSeat();
            break;
        }
      }
    });
  },

  /**
   * 显示编辑座位对话框
   */
  showEditSeat: function () {
    if (!this.data.currentSeat) return;
    
    this.setData({
      editVisible: true
    });
  },

  /**
   * 隐藏编辑座位对话框
   */
  hideEditSeat: function () {
    this.setData({
      editVisible: false
    });
  },

  /**
   * 保存编辑后的座位
   */
  saveEditSeat: function (e) {
    const formData = e.detail.value;
    const seatId = this.data.currentSeat.id;
    
    const seatDTO = {
      id: seatId,
      studyRoomId: this.data.studyRoomId,
      seatNumber: formData.seatNumber,
      rowNumber: parseInt(formData.rowNumber),
      columnNumber: parseInt(formData.columnNumber),
      hasPower: parseInt(formData.hasPower),
      isWindow: parseInt(formData.isWindow),
      isCorner: parseInt(formData.isCorner),
      status: parseInt(formData.status)
    };
    
    api.put(`/api/seats/${seatId}`, seatDTO).then(res => {
      if (res.success) {
        wx.showToast({
          title: '更新成功',
          icon: 'success',
          duration: 2000
        });
        
        this.hideEditSeat();
        this.loadSeats(); // 重新加载座位
      } else {
        wx.showToast({
          title: res.message || '更新座位失败',
          icon: 'none',
          duration: 2000
        });
      }
    }).catch(err => {
      console.error('更新座位异常', err);
      wx.showToast({
        title: '更新座位异常',
        icon: 'none',
        duration: 2000
      });
    });
  },

  /**
   * 切换座位状态
   */
  toggleSeatStatus: function () {
    if (!this.data.currentSeat) return;
    
    const seatId = this.data.currentSeat.id;
    const currentStatus = this.data.currentSeat.status;
    const newStatus = currentStatus === 1 ? 0 : 1;
    const statusText = newStatus === 1 ? '正常' : '停用';
    
    wx.showModal({
      title: '确认更改状态',
      content: `确定将座位 ${this.data.currentSeat.seatNumber} 状态更改为${statusText}吗？`,
      success: (res) => {
        if (res.confirm) {
          api.put(`/api/seats/${seatId}/status/${newStatus}`).then(res => {
            if (res.success) {
              wx.showToast({
                title: '状态更新成功',
                icon: 'success',
                duration: 2000
              });
              
              this.loadSeats(); // 重新加载座位
            } else {
              wx.showToast({
                title: res.message || '更新状态失败',
                icon: 'none',
                duration: 2000
              });
            }
          }).catch(err => {
            console.error('更新状态异常', err);
            wx.showToast({
              title: '更新状态异常',
              icon: 'none',
              duration: 2000
            });
          });
        }
      }
    });
  },

  /**
   * 确认删除座位
   */
  confirmDeleteSeat: function () {
    if (!this.data.currentSeat) return;
    
    const seatId = this.data.currentSeat.id;
    
    wx.showModal({
      title: '确认删除',
      content: `确定删除座位 ${this.data.currentSeat.seatNumber} 吗？此操作无法撤销！`,
      success: (res) => {
        if (res.confirm) {
          api.delete(`/api/seats/${seatId}`).then(res => {
            if (res.success) {
              wx.showToast({
                title: '删除成功',
                icon: 'success',
                duration: 2000
              });
              
              this.loadSeats(); // 重新加载座位
            } else {
              wx.showToast({
                title: res.message || '删除座位失败',
                icon: 'none',
                duration: 2000
              });
            }
          }).catch(err => {
            console.error('删除座位异常', err);
            wx.showToast({
              title: '删除座位异常',
              icon: 'none',
              duration: 2000
            });
          });
        }
      }
    });
  },

  /**
   * 显示生成座位对话框
   */
  showGenerateSeats: function () {
    this.setData({
      generateVisible: true
    });
  },

  /**
   * 隐藏生成座位对话框
   */
  hideGenerateSeats: function () {
    this.setData({
      generateVisible: false
    });
  },

  /**
   * 输入框值变化处理
   */
  inputChange: function (e) {
    const { field } = e.currentTarget.dataset;
    const value = e.detail.value;
    
    this.setData({
      [field]: value
    });
  },

  /**
   * 生成座位
   */
  generateSeats: function () {
    const { studyRoomId, rows, columns, hasPower } = this.data;
    
    if (!studyRoomId || rows <= 0 || columns <= 0) {
      wx.showToast({
        title: '参数错误',
        icon: 'none',
        duration: 2000
      });
      return;
    }
    
    wx.showModal({
      title: '确认生成座位',
      content: `将根据配置生成 ${rows}行${columns}列 共${rows * columns}个座位，此操作会清空当前自习室所有座位！确定继续吗？`,
      success: (res) => {
        if (res.confirm) {
          api.post('/api/seats/generate', null, {
            studyRoomId,
            rows,
            columns,
            hasPower
          }).then(res => {
            if (res.success) {
              wx.showToast({
                title: '生成成功',
                icon: 'success',
                duration: 2000
              });
              
              this.hideGenerateSeats();
              this.loadSeats(); // 重新加载座位
            } else {
              wx.showToast({
                title: res.message || '生成座位失败',
                icon: 'none',
                duration: 2000
              });
            }
          }).catch(err => {
            console.error('生成座位异常', err);
            wx.showToast({
              title: '生成座位异常',
              icon: 'none',
              duration: 2000
            });
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
      const seatQuery = { ...this.data.seatQuery };
      
      // 如果当前值等于点击的值，则取消筛选（设为null）
      if (seatQuery[type] === parseInt(value)) {
        seatQuery[type] = null;
      } else {
        seatQuery[type] = parseInt(value);
      }
      
      this.setData({
        seatQuery,
        currentPage: 1 // 重置到第一页
      });
      
      this.loadSeats();
    }
  },

  /**
   * 重置筛选条件
   */
  resetFilters: function () {
    this.setData({
      seatQuery: {
        hasPower: null,
        isWindow: null,
        isCorner: null,
        status: null
      },
      currentPage: 1 // 重置到第一页
    });
    
    this.loadSeats();
  },

  /**
   * 清空所有座位
   */
  clearAllSeats: function () {
    wx.showModal({
      title: '确认清空',
      content: '确定要清空该自习室的所有座位吗？此操作无法撤销！',
      success: (res) => {
        if (res.confirm) {
          api.delete(`/api/seats/study-room/${this.data.studyRoomId}`).then(res => {
            if (res.success) {
              wx.showToast({
                title: '清空成功',
                icon: 'success',
                duration: 2000
              });
              
              this.loadSeats(); // 重新加载座位
            } else {
              wx.showToast({
                title: res.message || '清空座位失败',
                icon: 'none',
                duration: 2000
              });
            }
          }).catch(err => {
            console.error('清空座位异常', err);
            wx.showToast({
              title: '清空座位异常',
              icon: 'none',
              duration: 2000
            });
          });
        }
      }
    });
  }
}) 