const app = getApp();
const { checkCodeApi, studyRoomApi } = require('../../../utils/api');

Page({
  data: {
    studyRoomList: [], // 自习室列表
    checkCodeList: [], // 签到码列表
    selectedDate: '', // 选择的日期
    selectedStudyRoom: null, // 选择的自习室
    loading: false,
    refreshing: false,
    currentPage: 1,
    pageSize: 10,
    hasMoreData: true,
    // 筛选选项
    showFilterModal: false,
    filters: {
      studyRoomId: null,
      validDate: null
    }
  },

  onLoad: function(options) {
    // 设置默认日期为今天
    const today = new Date();
    const dateStr = this.formatDate(today);
    this.setData({
      selectedDate: dateStr,
      'filters.validDate': dateStr
    });
    
    this.loadStudyRoomList();
    this.loadCheckCodeList();
  },

  onShow: function() {
    this.refreshData();
  },

  /**
   * 刷新数据
   */
  refreshData: function() {
    this.setData({
      refreshing: true,
      currentPage: 1,
      checkCodeList: []
    });
    this.loadCheckCodeList();
  },

  /**
   * 加载自习室列表
   */
  loadStudyRoomList: function() {
    studyRoomApi.getStudyRooms({
      current: 1,
      size: 100, // 获取所有自习室
      isActive: 1
    }).then(res => {
      if (res.code === 200) {
        this.setData({
          studyRoomList: res.data.records || []
        });
      } else {
        console.error('获取自习室列表失败:', res.message);
      }
    }).catch(err => {
      console.error('获取自习室列表错误:', err);
    });
  },

  /**
   * 加载签到码列表
   */
  loadCheckCodeList: function() {
    const { currentPage, pageSize, filters } = this.data;
    
    this.setData({
      loading: !this.data.refreshing
    });

    const params = {
      current: currentPage,
      size: pageSize
    };

    // 添加筛选条件
    if (filters.studyRoomId) {
      params.studyRoomId = filters.studyRoomId;
    }
    if (filters.validDate) {
      params.validDate = filters.validDate;
    }

    checkCodeApi.getCheckCodeList(params).then(res => {
      if (res.code === 200) {
        const pageData = res.data;
        const newList = pageData.records || [];
        
        this.setData({
          checkCodeList: currentPage === 1 ? newList : [...this.data.checkCodeList, ...newList],
          hasMoreData: currentPage < (pageData.pages || 1),
          loading: false,
          refreshing: false
        });
      } else {
        console.error('获取签到码列表失败:', res.message);
        wx.showToast({
          title: res.message || '获取签到码列表失败',
          icon: 'none'
        });
        this.setData({
          loading: false,
          refreshing: false
        });
      }
    }).catch(err => {
      console.error('获取签到码列表错误:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
      this.setData({
        loading: false,
        refreshing: false
      });
    });
  },

  /**
   * 生成今日签到码（所有自习室）
   */
  generateTodayCodesForAll: function() {
    wx.showModal({
      title: '确认操作',
      content: '确定要为所有自习室生成今日签到码吗？',
      success: (res) => {
        if (res.confirm) {
          this.doGenerateTodayCodesForAll();
        }
      }
    });
  },

  /**
   * 执行生成今日签到码
   */
  doGenerateTodayCodesForAll: function() {
    wx.showLoading({
      title: '生成中...'
    });

    const today = this.formatDate(new Date());
    
    checkCodeApi.generateDailyCodesForAllRooms(today).then(res => {
      wx.hideLoading();
      if (res.code === 200) {
        wx.showToast({
          title: `成功生成${res.data}个签到码`,
          icon: 'success'
        });
        this.refreshData();
      } else {
        wx.showToast({
          title: res.message || '生成失败',
          icon: 'none'
        });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('生成签到码错误:', err);
      wx.showToast({
        title: '生成失败',
        icon: 'error'
      });
    });
  },

  /**
   * 生成明日签到码（所有自习室）
   */
  generateTomorrowCodesForAll: function() {
    wx.showModal({
      title: '确认操作',
      content: '确定要为所有自习室生成明日签到码吗？',
      success: (res) => {
        if (res.confirm) {
          this.doGenerateTomorrowCodesForAll();
        }
      }
    });
  },

  /**
   * 执行生成明日签到码
   */
  doGenerateTomorrowCodesForAll: function() {
    wx.showLoading({
      title: '生成中...'
    });

    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const tomorrowStr = this.formatDate(tomorrow);
    
    checkCodeApi.generateDailyCodesForAllRooms(tomorrowStr).then(res => {
      wx.hideLoading();
      if (res.code === 200) {
        wx.showToast({
          title: `成功生成${res.data}个签到码`,
          icon: 'success'
        });
        this.refreshData();
      } else {
        wx.showToast({
          title: res.message || '生成失败',
          icon: 'none'
        });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('生成签到码错误:', err);
      wx.showToast({
        title: '生成失败',
        icon: 'error'
      });
    });
  },

  /**
   * 显示筛选弹窗
   */
  showFilter: function() {
    this.setData({
      showFilterModal: true
    });
  },

  /**
   * 隐藏筛选弹窗
   */
  hideFilter: function() {
    this.setData({
      showFilterModal: false
    });
  },

  /**
   * 选择自习室筛选
   */
  selectStudyRoom: function(e) {
    const studyRoomId = e.currentTarget.dataset.id;
    const studyRoom = this.data.studyRoomList.find(room => room.id === studyRoomId);
    
    this.setData({
      'filters.studyRoomId': studyRoomId,
      selectedStudyRoom: studyRoom
    });
  },

  /**
   * 清空自习室筛选
   */
  clearStudyRoomFilter: function() {
    this.setData({
      'filters.studyRoomId': null,
      selectedStudyRoom: null
    });
  },

  /**
   * 选择日期筛选
   */
  onDateChange: function(e) {
    this.setData({
      'filters.validDate': e.detail.value
    });
  },

  /**
   * 清空日期筛选
   */
  clearDateFilter: function() {
    this.setData({
      'filters.validDate': null
    });
  },

  /**
   * 应用筛选
   */
  applyFilter: function() {
    this.setData({
      showFilterModal: false,
      currentPage: 1,
      checkCodeList: []
    });
    this.loadCheckCodeList();
  },

  /**
   * 重置筛选
   */
  resetFilter: function() {
    this.setData({
      'filters.studyRoomId': null,
      'filters.validDate': null,
      selectedStudyRoom: null
    });
  },

  /**
   * 查看签到码详情
   */
  viewCheckCodeDetail: function(e) {
    const checkCode = e.currentTarget.dataset.checkcode;
    
    wx.showModal({
      title: '签到码详情',
      content: `自习室：${checkCode.studyRoomName}\n签到码：${checkCode.code}\n有效日期：${checkCode.validDate}\n状态：${checkCode.statusText}`,
      showCancel: false,
      confirmText: '确定'
    });
  },

  /**
   * 复制签到码
   */
  copyCheckCode: function(e) {
    const code = e.currentTarget.dataset.code;
    
    wx.setClipboardData({
      data: code,
      success: function() {
        wx.showToast({
          title: '签到码已复制',
          icon: 'success'
        });
      }
    });
  },

  /**
   * 分享签到码二维码
   */
  shareQRCode: function(e) {
    const checkCode = e.currentTarget.dataset.checkcode;
    
    // 这里可以生成二维码图片或者跳转到二维码展示页面
    wx.showToast({
      title: '二维码功能开发中',
      icon: 'none'
    });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh: function() {
    this.refreshData();
    setTimeout(() => {
      wx.stopPullDownRefresh();
    }, 1000);
  },

  /**
   * 上拉加载更多
   */
  onReachBottom: function() {
    if (this.data.hasMoreData && !this.data.loading) {
      this.setData({
        currentPage: this.data.currentPage + 1
      });
      this.loadCheckCodeList();
    }
  },

  /**
   * 格式化日期
   */
  formatDate: function(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  },

  /**
   * 格式化日期时间
   */
  formatDateTime: function(dateTimeStr) {
    if (!dateTimeStr) return '';
    const date = new Date(dateTimeStr);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  }
}); 