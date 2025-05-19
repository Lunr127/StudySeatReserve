// study-rooms.js
const { studyRoomApi } = require('../../utils/api');
const { getUserInfo } = require('../../utils/auth');

Page({
  data: {
    // 页面数据
    rooms: [],
    searchKeyword: '',
    showFilter: false,
    filter: {
      building: '',
      belongsTo: '',
      isActive: null
    },
    tempFilter: {},
    activeFilters: [],
    current: 1,
    size: 10,
    total: 0,
    loading: false,
    hasMore: true,
    isAdmin: false
  },
  
  onLoad: function(options) {
    // 页面加载时执行
    this.loadRooms();
    this.checkIsAdmin();
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
      if (userInfo && userInfo.userType === 1) {
        this.setData({
          isAdmin: true
        });
      }
    }).catch(err => {
      console.error('获取用户信息失败', err);
    });
  },
  
  // 加载自习室数据
  loadRooms: function() {
    this.setData({ loading: true });
    
    const params = {
      current: this.data.current,
      size: this.data.size,
      name: this.data.searchKeyword,
      building: this.data.filter.building,
      belongsTo: this.data.filter.belongsTo
    };
    
    // 只有当isActive不为null时才添加到请求参数中
    if (this.data.filter.isActive !== null) {
      params.isActive = this.data.filter.isActive;
    }
    
    return studyRoomApi.getStudyRooms(params)
      .then(res => {
        const { records = [], total = 0, current, size } = res.data || {};
        
        // 格式化时间显示
        const formattedRooms = records.map(room => {
          // 确保时间格式正确，处理不同类型的时间值
          let openTimeStr = '';
          let closeTimeStr = '';
          
          if (room.openTime) {
            // 处理可能的不同类型
            if (typeof room.openTime === 'string') {
              openTimeStr = room.openTime.substring(0, 5);
            } else if (room.openTime.toString) {
              openTimeStr = room.openTime.toString().substring(0, 5);
            }
          }
          
          if (room.closeTime) {
            // 处理可能的不同类型
            if (typeof room.closeTime === 'string') {
              closeTimeStr = room.closeTime.substring(0, 5);
            } else if (room.closeTime.toString) {
              closeTimeStr = room.closeTime.toString().substring(0, 5);
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
  
  // 显示筛选弹窗
  showFilterModal: function() {
    this.setData({
      showFilter: true,
      tempFilter: { ...this.data.filter }
    });
  },
  
  // 隐藏筛选弹窗
  hideFilterModal: function() {
    this.setData({
      showFilter: false
    });
  },
  
  // 选择筛选项
  selectFilter: function(e) {
    const { type, value } = e.currentTarget.dataset;
    const tempFilter = { ...this.data.tempFilter };
    
    if (type === 'isActive') {
      if (value === 'null') {
        tempFilter[type] = null;
      } else {
        tempFilter[type] = parseInt(value);
      }
    } else {
      tempFilter[type] = value;
    }
    
    this.setData({
      tempFilter
    });
  },
  
  // 重置筛选条件
  resetFilter: function() {
    this.setData({
      tempFilter: {
        building: '',
        belongsTo: '',
        isActive: null
      }
    });
  },
  
  // 确认筛选条件
  confirmFilter: function() {
    // 构建活跃筛选标签
    const activeFilters = [];
    
    if (this.data.tempFilter.building) {
      activeFilters.push({
        type: 'building',
        label: '建筑',
        value: this.data.tempFilter.building
      });
    }
    
    if (this.data.tempFilter.belongsTo) {
      activeFilters.push({
        type: 'belongsTo',
        label: '归属',
        value: this.data.tempFilter.belongsTo
      });
    }
    
    if (this.data.tempFilter.isActive !== null) {
      activeFilters.push({
        type: 'isActive',
        label: '状态',
        value: this.data.tempFilter.isActive === 1 ? '开放中' : '已关闭'
      });
    }
    
    this.setData({
      filter: this.data.tempFilter,
      activeFilters,
      showFilter: false,
      current: 1,
      rooms: []
    }, () => {
      this.loadRooms();
    });
  },
  
  // 移除筛选项
  removeFilter: function(e) {
    const index = e.currentTarget.dataset.index;
    const filter = { ...this.data.filter };
    const type = this.data.activeFilters[index].type;
    
    if (type === 'isActive') {
      filter[type] = null;
    } else {
      filter[type] = '';
    }
    
    const activeFilters = [...this.data.activeFilters];
    activeFilters.splice(index, 1);
    
    this.setData({
      filter,
      activeFilters,
      current: 1,
      rooms: []
    }, () => {
      this.loadRooms();
    });
  },
  
  // 清除所有筛选条件
  clearAllFilters: function() {
    this.setData({
      filter: {
        building: '',
        belongsTo: '',
        isActive: null
      },
      activeFilters: [],
      current: 1,
      rooms: []
    }, () => {
      this.loadRooms();
    });
  },
  
  // 跳转到自习室详情页
  goToRoomDetail: function(e) {
    const id = e.currentTarget.dataset.id;
    // 确保ID以字符串形式传递，防止JS大整数精度问题
    const stringId = String(id);
    wx.navigateTo({
      url: `/pages/study-rooms/detail/detail?id=${stringId}`
    });
  },
  
  // 跳转到管理页面
  goToManage: function() {
    wx.navigateTo({
      url: '/pages/admin/study-rooms/manage/manage'
    });
  },
  
  // 刷新自习室列表
  refreshRooms: function() {
    wx.showLoading({
      title: '刷新中...',
      mask: true
    });
    
    this.setData({
      rooms: [],
      current: 1,
      hasMore: true
    }, () => {
      this.loadRooms().then(() => {
        wx.hideLoading();
        wx.showToast({
          title: '刷新成功',
          icon: 'success',
          duration: 1500
        });
      }).catch(() => {
        wx.hideLoading();
      });
    });
  }
}) 