const app = getApp();
const { reservationApi } = require('../../../utils/api');

Page({
  data: {
    activeTab: 0, // 0: 当前预约, 1: 历史预约
    currentReservations: [],
    historyReservations: [],
    currentPage: 1,
    pageSize: 10,
    totalPages: 1,
    loading: true,
    refreshing: false,
    loadingMore: false,
    hasMoreData: true
  },
  
  onLoad: function() {
    console.log('页面加载，初始化当前预约数据');
    this.setData({
      activeTab: 0, // 确保是数字类型
      currentReservations: [],
      historyReservations: [],
      currentPage: 1,
      loading: true
    });
    this.loadCurrentReservations();
  },
  
  onShow: function() {
    console.log('页面显示，刷新当前Tab数据');
    // 页面显示时根据当前Tab刷新对应数据
    if (this.data.activeTab === 0) {
      this.refreshCurrentReservations();
    } else {
      this.refreshHistoryReservations();
    }
  },
  
  // 切换Tab
  changeTab: function(e) {
    const tab = parseInt(e.currentTarget.dataset.tab); // 转换为数字
    
    console.log('切换Tab:', { 
      oldTab: this.data.activeTab, 
      newTab: tab,
      tabType: typeof tab,
      currentReservationsLength: this.data.currentReservations.length,
      historyReservationsLength: this.data.historyReservations.length
    });
    
    if (tab === this.data.activeTab) {
      return;
    }
    
    this.setData({
      activeTab: tab
    });
    
    console.log('Tab切换完成，activeTab:', this.data.activeTab);
    
    // 根据选中的Tab加载不同数据
    if (tab === 0) {
      // 切换到当前预约，重置状态并加载
      this.setData({
        loading: true
      });
      this.loadCurrentReservations();
    } else {
      // 切换到历史预约，重置分页状态并加载
      this.setData({
        currentPage: 1,
        hasMoreData: true,
        historyReservations: [], // 清空历史数据重新加载
        loading: true
      });
      this.loadHistoryReservations();
    }
  },
  
  // 加载当前预约
  loadCurrentReservations: function() {
    console.log('开始加载当前预约');
    this.setData({
      loading: true
    });
    
    reservationApi.getCurrentReservations().then(res => {
      console.log('当前预约API响应:', res);
      if (res.code === 200) {
        // 格式化数据
        const currentReservations = (res.data || []).map(item => ({
          ...item,
          formattedStartTime: this.formatDateTime(item.startTime),
          formattedEndTime: this.formatDateTime(item.endTime),
          formattedCreateTime: this.formatDateTime(item.createTime)
        }));
        
        console.log('当前预约数据处理完成:', currentReservations);
        this.setData({
          currentReservations,
          loading: false,
          refreshing: false
        });
        
        // 添加调试日志，确认数据设置成功
        console.log('当前预约setData完成，当前状态:', {
          activeTab: this.data.activeTab,
          currentReservations: this.data.currentReservations,
          currentReservationsLength: this.data.currentReservations.length,
          loading: this.data.loading
        });
      } else {
        console.error('获取当前预约失败:', res.message);
        wx.showToast({
          title: res.message || '获取当前预约失败',
          icon: 'none'
        });
        this.setData({
          currentReservations: [],
          loading: false,
          refreshing: false
        });
      }
    }).catch(err => {
      console.error('获取当前预约错误:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
      this.setData({
        currentReservations: [],
        loading: false,
        refreshing: false
      });
    });
  },
  
  // 加载历史预约
  loadHistoryReservations: function() {
    const { currentPage, pageSize, historyReservations } = this.data;
    
    console.log('开始加载历史预约, 页码:', currentPage);
    this.setData({
      loading: !this.data.loadingMore,
      loadingMore: this.data.loadingMore
    });
    
    reservationApi.getReservations({
      current: currentPage,
      size: pageSize,
      status: [0, 3, 4] // 已取消、已完成、已违约
    }).then(res => {
      console.log('历史预约API响应:', res);
      if (res.code === 200) {
        const pageData = res.data;
        
        // 格式化数据
        const newData = (pageData.records || []).map(item => ({
          ...item,
          formattedStartTime: this.formatDateTime(item.startTime),
          formattedEndTime: this.formatDateTime(item.endTime),
          formattedCreateTime: this.formatDateTime(item.createTime)
        }));
        
        console.log('历史预约数据处理完成:', newData);
        
        // 判断是否还有更多数据
        const hasMoreData = currentPage < pageData.pages;
        
        this.setData({
          historyReservations: currentPage === 1 ? newData : [...historyReservations, ...newData],
          totalPages: pageData.pages || 1,
          loading: false,
          refreshing: false,
          loadingMore: false,
          hasMoreData
        });
        
        // 添加调试日志，确认数据设置成功
        console.log('历史预约setData完成，当前状态:', {
          activeTab: this.data.activeTab,
          historyReservations: this.data.historyReservations,
          historyReservationsLength: this.data.historyReservations.length,
          loading: this.data.loading
        });
      } else {
        console.error('获取历史预约失败:', res.message);
        wx.showToast({
          title: res.message || '获取历史预约失败',
          icon: 'none'
        });
        this.setData({
          loading: false,
          refreshing: false,
          loadingMore: false
        });
      }
    }).catch(err => {
      console.error('获取历史预约错误:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
      this.setData({
        loading: false,
        refreshing: false,
        loadingMore: false
      });
    });
  },
  
  // 下拉刷新当前预约
  refreshCurrentReservations: function() {
    this.setData({
      refreshing: true
    });
    this.loadCurrentReservations();
  },
  
  // 下拉刷新历史预约
  refreshHistoryReservations: function() {
    this.setData({
      refreshing: true,
      currentPage: 1
    });
    this.loadHistoryReservations();
  },
  
  // 上拉加载更多历史预约
  loadMoreHistoryReservations: function() {
    if (!this.data.hasMoreData || this.data.loadingMore) {
      return;
    }
    
    this.setData({
      currentPage: this.data.currentPage + 1,
      loadingMore: true
    });
    
    this.loadHistoryReservations();
  },
  
  // 取消预约
  cancelReservation: function(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '取消预约',
      content: '确定要取消该预约吗？',
      success: (res) => {
        if (res.confirm) {
          this.doCancelReservation(id);
        }
      }
    });
  },
  
  // 执行取消预约
  doCancelReservation: function(id) {
    wx.showLoading({
      title: '取消中...',
    });
    
    reservationApi.cancelReservation(id).then(res => {
      wx.hideLoading();
      
      if (res.code === 200) {
        wx.showToast({
          title: '取消成功',
          icon: 'success'
        });
        // 刷新列表
        this.refreshCurrentReservations();
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
  
  // 延长预约
  extendReservation: function(e) {
    const { id, endTime } = e.currentTarget.dataset;
    
    // 跳转到延长预约页面
    wx.navigateTo({
      url: `/pages/user-center/extend-reservation/extend-reservation?id=${id}&endTime=${endTime}`
    });
  },

  // 跳转到签到页面
  goToCheckIn: function(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.navigateTo({
      url: `/pages/check-in/check-in?reservationId=${id}`
    });
  },
  
  // 查看预约详情
  viewReservationDetail: function(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.navigateTo({
      url: `/pages/user-center/reservation-detail/reservation-detail?id=${id}`
    });
  },
  
  // 下拉刷新
  onPullDownRefresh: function() {
    if (this.data.activeTab === 0) {
      this.refreshCurrentReservations();
    } else {
      this.refreshHistoryReservations();
    }
    
    // 停止下拉刷新动画
    wx.stopPullDownRefresh();
  },
  
  // 上拉触底
  onReachBottom: function() {
    if (this.data.activeTab === 1) {
      this.loadMoreHistoryReservations();
    }
  },
  
  // 格式化时间：2023-10-01 14:30
  formatDateTime: function(dateTimeStr) {
    if (!dateTimeStr) return '';
    
    const date = new Date(dateTimeStr);
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  },
  
  // 判断是否可以签到
  canCheckIn: function(reservation) {
    return reservation.canCheckIn;
  },
  
  // 判断是否可以取消
  canCancel: function(reservation) {
    return reservation.canCancel;
  },
  
  // 判断是否可以延长
  canExtend: function(reservation) {
    return reservation.canExtend;
  },
  
  // 获取状态样式类
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