// notifications.js
const app = getApp();
const { notificationApi } = require('../../utils/api');

Page({
  data: {
    notificationList: [],
    unreadCount: 0,
    loading: true,
    refreshing: false,
    loadingMore: false,
    hasMoreData: true,
    currentPage: 1,
    pageSize: 10,
    // 筛选条件
    activeTab: 'all', // all, unread, read
    showFilterModal: false,
    filterType: null, // 通知类型筛选
    typeOptions: [
      { value: null, label: '全部类型' },
      { value: 1, label: '系统通知' },
      { value: 2, label: '预约提醒' },
      { value: 3, label: '迟到提醒' },
      { value: 4, label: '违约通知' }
    ]
  },

  onLoad: function(options) {
    // 检查用户类型，只有学生用户才能访问通知页面
    const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo');
    if (!userInfo || userInfo.userType !== 2) {
      wx.showToast({
        title: '无访问权限',
        icon: 'none'
      });
      wx.switchTab({
        url: '/pages/index/index'
      });
      return;
    }
    
    this.loadNotifications();
    this.loadUnreadCount();
  },

  onShow: function() {
    // 页面显示时刷新通知和未读数量
    this.refreshNotifications();
    this.loadUnreadCount();
  },

  /**
   * 加载通知列表
   */
  loadNotifications: function() {
    const { currentPage, pageSize, activeTab, filterType } = this.data;

    this.setData({
      loading: !this.data.loadingMore
    });

    const params = {
      current: currentPage,
      size: pageSize
    };

    // 根据Tab设置已读状态筛选
    if (activeTab === 'unread') {
      params.isRead = 0;
    } else if (activeTab === 'read') {
      params.isRead = 1;
    }

    // 设置类型筛选
    if (filterType) {
      params.type = filterType;
    }

    notificationApi.getUserNotifications(params).then(res => {
      if (res.code === 200) {
        const pageData = res.data;
        const newData = pageData.records || [];

        // 处理数据
        const processedData = newData.map(item => ({
          ...item,
          formattedCreateTime: this.formatDateTime(item.createTime),
          typeClass: this.getTypeClass(item.type),
          isUnread: item.isRead === 0
        }));

        // 判断是否还有更多数据
        const hasMoreData = currentPage < pageData.pages;

        this.setData({
          notificationList: currentPage === 1 ? processedData : [...this.data.notificationList, ...processedData],
          hasMoreData,
          loading: false,
          refreshing: false,
          loadingMore: false
        });
      } else {
        wx.showToast({
          title: res.message || '获取通知失败',
          icon: 'none'
        });
        this.setData({
          loading: false,
          refreshing: false,
          loadingMore: false
        });
      }
    }).catch(err => {
      console.error('获取通知列表错误:', err);
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

  /**
   * 加载未读通知数量
   */
  loadUnreadCount: function() {
    notificationApi.getUnreadCount().then(res => {
      if (res.code === 200) {
        this.setData({
          unreadCount: res.data || 0
        });
        
        // 更新TabBar红点
        if (res.data > 0) {
          wx.showTabBarRedDot({
            index: 0 // 首页Tab索引
          });
        } else {
          wx.hideTabBarRedDot({
            index: 0
          });
        }
      }
    }).catch(err => {
      console.error('获取未读通知数量错误:', err);
    });
  },

  /**
   * 刷新通知列表
   */
  refreshNotifications: function() {
    this.setData({
      refreshing: true,
      currentPage: 1,
      hasMoreData: true
    });
    this.loadNotifications();
  },

  /**
   * 加载更多通知
   */
  loadMoreNotifications: function() {
    if (!this.data.hasMoreData || this.data.loadingMore) {
      return;
    }

    this.setData({
      currentPage: this.data.currentPage + 1,
      loadingMore: true
    });

    this.loadNotifications();
  },

  /**
   * 切换Tab
   */
  switchTab: function(e) {
    const tab = e.currentTarget.dataset.tab;
    
    if (tab === this.data.activeTab) {
      return;
    }

    this.setData({
      activeTab: tab,
      currentPage: 1,
      notificationList: []
    });

    this.loadNotifications();
  },

  /**
   * 点击通知项
   */
  onNotificationTap: function(e) {
    const { notification } = e.currentTarget.dataset;
    
    // 如果未读，先标记为已读
    if (notification.isRead === 0) {
      this.markAsRead(notification.id);
    }

    // 显示通知详情
    this.showNotificationDetail(notification);
  },

  /**
   * 标记通知为已读
   */
  markAsRead: function(notificationId) {
    notificationApi.markAsRead(notificationId).then(res => {
      if (res.code === 200) {
        // 更新本地数据
        const notificationList = this.data.notificationList.map(item => {
          if (item.id === notificationId) {
            item.isRead = 1;
            item.isUnread = false;
          }
          return item;
        });

        this.setData({
          notificationList
        });

        // 刷新未读数量
        this.loadUnreadCount();
      }
    }).catch(err => {
      console.error('标记通知为已读失败:', err);
    });
  },

  /**
   * 标记所有通知为已读
   */
  markAllAsRead: function() {
    wx.showModal({
      title: '确认操作',
      content: '确定要标记所有通知为已读吗？',
      success: (res) => {
        if (res.confirm) {
          this.doMarkAllAsRead();
        }
      }
    });
  },

  /**
   * 执行标记所有为已读
   */
  doMarkAllAsRead: function() {
    wx.showLoading({
      title: '处理中...'
    });

    notificationApi.markAllAsRead().then(res => {
      wx.hideLoading();
      
      if (res.code === 200) {
        wx.showToast({
          title: '操作成功',
          icon: 'success'
        });

        // 刷新列表
        this.refreshNotifications();
        this.loadUnreadCount();
      } else {
        wx.showToast({
          title: res.message || '操作失败',
          icon: 'none'
        });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('标记所有通知为已读失败:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
    });
  },

  /**
   * 删除通知
   */
  deleteNotification: function(e) {
    e.stopPropagation(); // 阻止冒泡
    
    const { id } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条通知吗？',
      success: (res) => {
        if (res.confirm) {
          this.doDeleteNotification(id);
        }
      }
    });
  },

  /**
   * 执行删除通知
   */
  doDeleteNotification: function(notificationId) {
    notificationApi.deleteNotification(notificationId).then(res => {
      if (res.code === 200) {
        wx.showToast({
          title: '删除成功',
          icon: 'success'
        });

        // 从列表中移除
        const notificationList = this.data.notificationList.filter(item => item.id !== notificationId);
        this.setData({
          notificationList
        });

        // 刷新未读数量
        this.loadUnreadCount();
      } else {
        wx.showToast({
          title: res.message || '删除失败',
          icon: 'none'
        });
      }
    }).catch(err => {
      console.error('删除通知失败:', err);
      wx.showToast({
        title: '网络错误',
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
   * 选择筛选类型
   */
  selectFilterType: function(e) {
    const { value } = e.currentTarget.dataset;
    
    this.setData({
      filterType: value,
      showFilterModal: false,
      currentPage: 1,
      notificationList: []
    });

    this.loadNotifications();
  },

  /**
   * 显示通知详情
   */
  showNotificationDetail: function(notification) {
    wx.showModal({
      title: notification.title,
      content: notification.content,
      showCancel: false,
      confirmText: '知道了'
    });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh: function() {
    this.refreshNotifications();
    wx.stopPullDownRefresh();
  },

  /**
   * 上拉触底
   */
  onReachBottom: function() {
    this.loadMoreNotifications();
  },

  /**
   * 格式化时间
   */
  formatDateTime: function(dateTimeStr) {
    if (!dateTimeStr) return '';
    
    const date = new Date(dateTimeStr);
    const now = new Date();
    const diff = now - date;
    
    // 如果是今天
    if (diff < 24 * 60 * 60 * 1000 && date.getDate() === now.getDate()) {
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `今天 ${hours}:${minutes}`;
    }
    
    // 如果是昨天
    const yesterday = new Date(now);
    yesterday.setDate(yesterday.getDate() - 1);
    if (date.getDate() === yesterday.getDate() && 
        date.getMonth() === yesterday.getMonth() && 
        date.getFullYear() === yesterday.getFullYear()) {
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `昨天 ${hours}:${minutes}`;
    }
    
    // 其他情况显示完整日期
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  },

  /**
   * 获取通知类型样式类
   */
  getTypeClass: function(type) {
    const typeMap = {
      1: 'type-system',      // 系统通知
      2: 'type-reservation', // 预约提醒
      3: 'type-late',        // 迟到提醒
      4: 'type-violation'    // 违约通知
    };
    
    return typeMap[type] || 'type-default';
  }
}); 