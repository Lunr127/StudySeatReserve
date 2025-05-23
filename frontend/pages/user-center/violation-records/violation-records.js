const app = getApp();

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

    // 模拟违约记录数据，实际应调用后端API
    setTimeout(() => {
      const mockData = [
        {
          id: 1,
          reservationId: 101,
          studyRoomName: '中心图书馆一楼自习室',
          seatNumber: 'A12',
          violationType: 1, // 1-未签到，2-迟到，3-提前离开
          violationTypeText: '未签到',
          reservationTime: '2023-10-15 14:00-16:00',
          description: '预约后未在规定时间内签到',
          createTime: '2023-10-15 14:15'
        },
        {
          id: 2,
          reservationId: 102,
          studyRoomName: '工学院自习室',
          seatNumber: 'B05',
          violationType: 2,
          violationTypeText: '迟到',
          reservationTime: '2023-10-12 09:00-11:00',
          description: '预约时间开始后20分钟才签到',
          createTime: '2023-10-12 09:20'
        }
      ];

      this.setData({
        violationList: this.data.currentPage === 1 ? mockData : [...this.data.violationList, ...mockData],
        loading: false,
        loadingMore: false,
        isEmpty: mockData.length === 0,
        hasMoreData: mockData.length >= this.data.pageSize
      });
    }, 1000);
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
   * 获取违约类型样式
   */
  getViolationTypeClass: function(type) {
    const typeMap = {
      1: 'type-no-show',     // 未签到
      2: 'type-late',        // 迟到
      3: 'type-early-leave'  // 提前离开
    };
    
    return typeMap[type] || '';
  }
}); 