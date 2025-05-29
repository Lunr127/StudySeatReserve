const app = getApp();
const { userApi } = require('../../../utils/api');

Page({
  data: {
    violationList: [],
    loading: true,
    isEmpty: false,
    currentPage: 1,
    pageSize: 10,
    hasMoreData: true,
    loadingMore: false
  },

  onLoad: function(options) {
    // 检查用户类型，只有学生用户才能访问违约记录页面
    const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo');
    if (!userInfo || userInfo.userType !== 2) {
      wx.showToast({
        title: '无访问权限',
        icon: 'none'
      });
      wx.switchTab({
        url: '/pages/user-center/user-center'
      });
      return;
    }
    
    this.loadViolationRecords();
  },

  onShow: function() {
    // 页面显示时刷新违约记录
    this.refreshViolationRecords();
  },

  /**
   * 加载违约记录
   */
  loadViolationRecords: function() {
    this.setData({
      loading: !this.data.loadingMore
    });

    // 调用后端API获取违约记录
    userApi.getUserViolations({
      current: this.data.currentPage,
      size: this.data.pageSize
    }).then(res => {
      if (res.code === 200) {
        const pageData = res.data;
        const newData = pageData.records || [];
        
        // 处理违约类型文字描述
        const processedData = newData.map(item => {
          return {
            ...item,
            violationTypeText: this.getViolationTypeText(item.violationType),
            createTime: this.formatDateTime(item.createTime)
          };
        });
        
        // 判断是否还有更多数据
        const hasMoreData = this.data.currentPage < pageData.pages;
        
        this.setData({
          violationList: this.data.currentPage === 1 ? processedData : [...this.data.violationList, ...processedData],
          loading: false,
          loadingMore: false,
          isEmpty: processedData.length === 0 && this.data.currentPage === 1,
          hasMoreData
        });
      } else {
        wx.showToast({
          title: res.message || '获取违约记录失败',
          icon: 'none'
        });
        this.setData({
          loading: false,
          loadingMore: false,
          isEmpty: true
        });
      }
    }).catch(err => {
      console.error('获取违约记录错误:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
      this.setData({
        loading: false,
        loadingMore: false,
        isEmpty: true
      });
    });
  },

  /**
   * 刷新违约记录
   */
  refreshViolationRecords: function() {
    this.setData({
      currentPage: 1,
      hasMoreData: true
    });
    this.loadViolationRecords();
  },

  /**
   * 加载更多违约记录
   */
  loadMoreViolationRecords: function() {
    if (!this.data.hasMoreData || this.data.loadingMore) {
      return;
    }

    this.setData({
      currentPage: this.data.currentPage + 1,
      loadingMore: true
    });

    this.loadViolationRecords();
  },

  /**
   * 查看违约详情
   */
  viewViolationDetail: function(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.navigateTo({
      url: `/pages/user-center/violation-detail/violation-detail?id=${id}`
    });
  },

  /**
   * 申诉违约
   */
  appealViolation: function(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '申诉违约',
      content: '确定要对此违约记录进行申诉吗？',
      success: (res) => {
        if (res.confirm) {
          // 跳转到申诉页面
          wx.navigateTo({
            url: `/pages/user-center/violation-appeal/violation-appeal?id=${id}`
          });
        }
      }
    });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh: function() {
    this.refreshViolationRecords();
    wx.stopPullDownRefresh();
  },

  /**
   * 上拉触底
   */
  onReachBottom: function() {
    this.loadMoreViolationRecords();
  },

  /**
   * 获取违约类型文字描述
   */
  getViolationTypeText: function(type) {
    const typeMap = {
      1: '未签到',
      2: '迟到', 
      3: '提前离开'
    };
    
    return typeMap[type] || '未知';
  },

  /**
   * 获取违约类型样式类
   */
  getViolationTypeClass: function(type) {
    const typeMap = {
      1: 'type-no-show',     // 未签到
      2: 'type-late',        // 迟到
      3: 'type-early-leave'  // 提前离开
    };
    
    return typeMap[type] || '';
  },

  /**
   * 格式化时间
   */
  formatDateTime: function(dateTimeStr) {
    if (!dateTimeStr) return '';
    
    const date = new Date(dateTimeStr);
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  }
}); 