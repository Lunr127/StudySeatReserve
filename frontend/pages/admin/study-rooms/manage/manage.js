const { studyRoomApi } = require('../../../../utils/api');
const { getUserInfo } = require('../../../../utils/auth');

Page({
  data: {
    rooms: [],
    searchKeyword: '',
    filterType: 'all', // all, active, inactive
    current: 1,
    size: 10,
    total: 0,
    loading: false,
    hasMore: true
  },
  
  onLoad: function(options) {
    // 检查是否管理员
    this.checkIsAdmin();
    
    // 加载数据
    this.loadRooms();
  },
  
  onShow: function() {
    // 页面每次显示时刷新数据
    this.setData({
      current: 1,
      rooms: []
    });
    this.loadRooms();
  },
  
  onPullDownRefresh: function() {
    // 下拉刷新
    this.setData({
      rooms: [],
      current: 1,
      hasMore: true
    });
    this.loadRooms().then(() => {
      wx.stopPullDownRefresh();
    });
  },
  
  onReachBottom: function() {
    // 上拉加载更多
    if (this.data.hasMore && !this.data.loading) {
      this.loadMore();
    }
  },
  
  // 检查用户是否为管理员
  checkIsAdmin: function() {
    getUserInfo().then(userInfo => {
      if (!userInfo || userInfo.userType !== 1) {
        wx.showToast({
          title: '您没有管理员权限',
          icon: 'none'
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
    }).catch(err => {
      console.error('获取用户信息失败', err);
      wx.showToast({
        title: '获取用户信息失败',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    });
  },
  
  // 加载自习室数据
  loadRooms: function() {
    this.setData({ loading: true });
    
    const params = {
      current: this.data.current,
      size: this.data.size,
      name: this.data.searchKeyword
    };
    
    // 根据筛选条件设置状态参数
    if (this.data.filterType === 'active') {
      params.isActive = 1;
    } else if (this.data.filterType === 'inactive') {
      params.isActive = 0;
    }
    
    return studyRoomApi.getStudyRooms(params)
      .then(res => {
        const { records = [], total = 0, current, size } = res.data || {};
        
        // 格式化时间显示
        const formattedRooms = records.map(room => {
          // 处理开放时间
          let openTimeStr = '';
          if (room.openTime) {
            // 处理可能的不同类型
            if (typeof room.openTime === 'string') {
              // 检查是否已经是 HH:MM 格式
              if (room.openTime.includes(':')) {
                openTimeStr = room.openTime.substring(0, 5);
              } else if (room.openTime.includes(',')) {
                // 将 "8,0" 转换为 "08:00" 格式
                const [hours, minutes] = room.openTime.split(',').map(Number);
                openTimeStr = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
              } else {
                openTimeStr = room.openTime.substring(0, 5);
              }
            } else if (room.openTime.toString) {
              const timeStr = room.openTime.toString();
              // 检查是否包含逗号（如 "8,0"）
              if (timeStr.includes(',')) {
                const [hours, minutes] = timeStr.split(',').map(Number);
                openTimeStr = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
              } else if (timeStr.includes(':')) {
                openTimeStr = timeStr.substring(0, 5);
              } else {
                openTimeStr = timeStr.substring(0, 5);
              }
            }
          }
          
          // 处理关闭时间
          let closeTimeStr = '';
          if (room.closeTime) {
            // 处理可能的不同类型
            if (typeof room.closeTime === 'string') {
              // 检查是否已经是 HH:MM 格式
              if (room.closeTime.includes(':')) {
                closeTimeStr = room.closeTime.substring(0, 5);
              } else if (room.closeTime.includes(',')) {
                // 将 "22,0" 转换为 "22:00" 格式
                const [hours, minutes] = room.closeTime.split(',').map(Number);
                closeTimeStr = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
              } else {
                closeTimeStr = room.closeTime.substring(0, 5);
              }
            } else if (room.closeTime.toString) {
              const timeStr = room.closeTime.toString();
              // 检查是否包含逗号（如 "22,0"）
              if (timeStr.includes(',')) {
                const [hours, minutes] = timeStr.split(',').map(Number);
                closeTimeStr = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
              } else if (timeStr.includes(':')) {
                closeTimeStr = timeStr.substring(0, 5);
              } else {
                closeTimeStr = timeStr.substring(0, 5);
              }
            }
          }
          
          return {
            ...room,
            openTime: openTimeStr,
            closeTime: closeTimeStr
          };
        });
        
        // 更新数据
        this.setData({
          rooms: this.data.current === 1 ? formattedRooms : [...this.data.rooms, ...formattedRooms],
          total,
          hasMore: current * size < total,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载自习室数据失败', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        });
      });
  },
  
  // 加载更多
  loadMore: function() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({
        current: this.data.current + 1
      }, () => {
        this.loadRooms();
      });
    }
  },
  
  // 搜索输入
  onSearchInput: function(e) {
    this.setData({
      searchKeyword: e.detail.value
    });
    
    // 防抖处理
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
    
    this.searchTimer = setTimeout(() => {
      this.setData({
        current: 1,
        rooms: []
      });
      this.loadRooms();
    }, 500);
  },
  
  // 切换筛选条件
  switchFilter: function(e) {
    const type = e.currentTarget.dataset.type;
    if (type !== this.data.filterType) {
      this.setData({
        filterType: type,
        current: 1,
        rooms: []
      }, () => {
        this.loadRooms();
      });
    }
  },
  
  // 跳转到自习室详情
  goToDetail: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/study-rooms/detail/detail?id=${String(id)}`
    });
  },
  
  // 跳转到编辑页面
  goToEditRoom: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/admin/study-rooms/edit/edit?id=${String(id)}`
    });
  },
  
  // 跳转到新增页面
  goToAddRoom: function() {
    wx.navigateTo({
      url: `/pages/admin/study-rooms/edit/edit`
    });
  },
  
  // 切换自习室状态
  toggleStatus: function(e) {
    const id = e.currentTarget.dataset.id;
    const currentStatus = parseInt(e.currentTarget.dataset.status);
    const newStatus = currentStatus === 1 ? 0 : 1;
    
    wx.showModal({
      title: '确认操作',
      content: `确定要${newStatus === 1 ? '开放' : '关闭'}该自习室吗？`,
      success: (res) => {
        if (res.confirm) {
          studyRoomApi.updateStatus(String(id), newStatus)
            .then(res => {
              if (res.code === 200) {
                wx.showToast({
                  title: '操作成功',
                  icon: 'success'
                });
                
                // 更新本地状态
                const rooms = [...this.data.rooms];
                const index = rooms.findIndex(room => room.id === id);
                if (index !== -1) {
                  rooms[index].isActive = newStatus;
                  this.setData({
                    rooms
                  });
                }
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
  
  // 删除自习室
  deleteRoom: function(e) {
    const id = e.currentTarget.dataset.id;
    
    wx.showModal({
      title: '警告',
      content: '确定要删除此自习室吗？此操作不可恢复！',
      confirmColor: '#f5222d',
      success: (res) => {
        if (res.confirm) {
          studyRoomApi.deleteStudyRoom(String(id))
            .then(res => {
              if (res.code === 200) {
                wx.showToast({
                  title: '删除成功',
                  icon: 'success'
                });
                
                // 从本地列表中移除
                const rooms = this.data.rooms.filter(room => room.id !== id);
                this.setData({
                  rooms
                });
              } else {
                wx.showToast({
                  title: res.message || '操作失败',
                  icon: 'none'
                });
              }
            })
            .catch(err => {
              console.error('删除自习室失败', err);
              wx.showToast({
                title: '操作失败，请重试',
                icon: 'none'
              });
            });
        }
      }
    });
  }
}); 