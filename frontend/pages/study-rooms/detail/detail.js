const { studyRoomApi, seatApi } = require('../../../utils/api');
const { getUserInfo } = require('../../../utils/auth');

Page({
  data: {
    room: {},
    seats: [],
    loading: true,
    isAdmin: false,
    isFavorite: false
  },
  
  onLoad: function(options) {
    // 获取传入的自习室ID
    if (options.id) {
      // 确保ID以字符串形式处理，防止JS大整数精度问题
      const roomId = String(options.id);
      console.log('获取到自习室ID:', roomId);
      
      this.setData({
        roomId: roomId
      });
      this.loadRoomDetail(roomId);
      this.loadSeatPreview(roomId);
      this.checkIsAdmin();
      this.checkIsFavorite(roomId);
    } else {
      wx.showToast({
        title: '自习室ID不能为空',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },
  
  onPullDownRefresh: function() {
    // 下拉刷新
    this.loadRoomDetail(this.data.roomId);
    this.loadSeatPreview(this.data.roomId);
    wx.stopPullDownRefresh();
  },
  
  // 检查用户是否为管理员
  checkIsAdmin: function() {
    getUserInfo().then(userInfo => {
      if (userInfo && userInfo.userType === 1) {
        this.setData({
          isAdmin: true
        });
      }
    }).catch(err => {
      console.error('获取用户信息失败', err);
    });
  },
  
  // 检查自习室是否已收藏（此处仅为示例，需要后端实现）
  checkIsFavorite: function(roomId) {
    this.setData({
      isFavorite: false
    });
  },
  
  // 加载自习室详情
  loadRoomDetail: function(id) {
    this.setData({ loading: true });
    
    // 确保ID为字符串，防止JS大整数精度问题
    const stringId = String(id);
    console.log('正在加载自习室详情, ID:', stringId);
    
    // 明确以字符串形式传递
    studyRoomApi.getStudyRoomDetail(stringId)
      .then(res => {
        console.log('自习室详情返回数据:', res);
        if (res.code === 200 && res.data) {
          // 格式化时间显示
          const roomData = res.data;
          
          // 安全处理时间格式
          let openTimeStr = '';
          let closeTimeStr = '';
          
          if (roomData.openTime) {
            // 处理可能的不同类型
            if (typeof roomData.openTime === 'string') {
              openTimeStr = roomData.openTime.substring(0, 5);
            } else if (roomData.openTime.toString) {
              openTimeStr = roomData.openTime.toString().substring(0, 5);
            }
          }
          
          if (roomData.closeTime) {
            // 处理可能的不同类型
            if (typeof roomData.closeTime === 'string') {
              closeTimeStr = roomData.closeTime.substring(0, 5);
            } else if (roomData.closeTime.toString) {
              closeTimeStr = roomData.closeTime.toString().substring(0, 5);
            }
          }
          
          roomData.openTime = openTimeStr;
          roomData.closeTime = closeTimeStr;
          
          this.setData({
            room: roomData,
            loading: false
          });
          
          // 设置导航栏标题
          wx.setNavigationBarTitle({
            title: roomData.name || '自习室详情'
          });
        } else {
          const errorMsg = res.message || '自习室不存在或已删除';
          console.error('加载自习室详情失败:', errorMsg);
          wx.showToast({
            title: errorMsg,
            icon: 'none'
          });
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        }
      })
      .catch(err => {
        console.error('加载自习室详情失败', err);
        this.setData({ loading: false });
        
        // 提取更详细的错误信息
        let errorMsg = '加载失败，请重试';
        if (err && err.message) {
          errorMsg = err.message;
        } else if (typeof err === 'string') {
          errorMsg = err;
        } else if (err && err.code && err.code === 500) {
          errorMsg = '服务器错误，请联系管理员';
        }
        
        wx.showToast({
          title: errorMsg,
          icon: 'none'
        });
      });
  },
  
  // 加载座位预览
  loadSeatPreview: function(roomId) {
    // 确保ID以字符串形式传递，防止JS大整数精度问题
    const stringId = String(roomId);
    seatApi.getSeatsByRoomId(stringId, { current: 1, size: 12 })
      .then(res => {
        if (res.code === 200 && res.data) {
          const seats = res.data.records || [];
          console.log('加载座位预览成功', seats);
          this.setData({
            seats: seats
          });
        } else {
          console.warn('加载座位预览返回异常数据', res);
        }
      })
      .catch(err => {
        console.error('加载座位预览失败', err);
        // 座位预览加载失败不影响整体页面显示，只是提示
        wx.showToast({
          title: '座位信息加载失败',
          icon: 'none',
          duration: 2000
        });
      });
  },
  
  // 编辑自习室（管理员功能）
  editRoom: function() {
    // 确保ID以字符串形式传递，防止JS大整数精度问题
    const stringId = String(this.data.roomId);
    wx.navigateTo({
      url: `/pages/admin/study-rooms/edit/edit?id=${stringId}`
    });
  },
  
  // 切换自习室状态（管理员功能）
  toggleStatus: function() {
    const newStatus = this.data.room.isActive === 1 ? 0 : 1;
    wx.showModal({
      title: '确认操作',
      content: `确定要${newStatus === 1 ? '开放' : '关闭'}该自习室吗？`,
      success: (res) => {
        if (res.confirm) {
          // 确保ID以字符串形式传递，防止JS大整数精度问题
          const stringId = String(this.data.roomId);
          studyRoomApi.updateStatus(stringId, newStatus)
            .then(res => {
              if (res.code === 200) {
                wx.showToast({
                  title: '操作成功',
                  icon: 'success'
                });
                
                // 更新本地状态
                const room = { ...this.data.room };
                room.isActive = newStatus;
                this.setData({
                  room
                });
              } else {
                wx.showToast({
                  title: res.message || '操作失败',
                  icon: 'none'
                });
              }
            })
            .catch(err => {
              console.error('更新自习室状态失败', err);
              wx.showToast({
                title: '操作失败，请重试',
                icon: 'none'
              });
            });
        }
      }
    });
  },
  
  // 删除自习室（管理员功能）
  deleteRoom: function() {
    wx.showModal({
      title: '警告',
      content: '确定要删除此自习室吗？此操作不可恢复！',
      confirmColor: '#f5222d',
      success: (res) => {
        if (res.confirm) {
          // 确保ID以字符串形式传递，防止JS大整数精度问题
          const stringId = String(this.data.roomId);
          studyRoomApi.deleteStudyRoom(stringId)
            .then(res => {
              if (res.code === 200) {
                wx.showToast({
                  title: '删除成功',
                  icon: 'success'
                });
                setTimeout(() => {
                  wx.navigateBack();
                }, 1500);
              } else {
                wx.showToast({
                  title: res.message || '删除失败',
                  icon: 'none'
                });
              }
            })
            .catch(err => {
              console.error('删除自习室失败', err);
              wx.showToast({
                title: '删除失败，请重试',
                icon: 'none'
              });
            });
        }
      }
    });
  },
  
  // 跳转到预约座位页面
  goToReservation: function() {
    // 确保ID以字符串形式传递，防止JS大整数精度问题
    const stringId = String(this.data.roomId);
    wx.navigateTo({
      url: `/pages/seat-reservation/seat-selection/seat-selection?roomId=${stringId}`
    });
  },
  
  // 收藏自习室
  addToFavorite: function() {
    // 此处需要调用后端API添加收藏
    this.setData({
      isFavorite: true
    });
    wx.showToast({
      title: '收藏成功',
      icon: 'success'
    });
  },
  
  // 取消收藏
  removeFromFavorite: function() {
    // 此处需要调用后端API取消收藏
    this.setData({
      isFavorite: false
    });
    wx.showToast({
      title: '已取消收藏',
      icon: 'success'
    });
  }
}) 