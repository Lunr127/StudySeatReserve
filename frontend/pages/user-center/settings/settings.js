const app = getApp();

Page({
  data: {
    userInfo: null,
    settingItems: [
      {
        id: 'notification',
        title: '消息通知',
        subtitle: '预约提醒、迟到提醒等',
        type: 'switch',
        value: true
      },
      {
        id: 'auto-cancel',
        title: '自动取消预约',
        subtitle: '超时未签到自动取消',
        type: 'switch',
        value: false
      },
      {
        id: 'privacy',
        title: '隐私设置',
        subtitle: '个人信息保护',
        type: 'navigate',
        url: '/pages/user-center/privacy/privacy'
      },
      {
        id: 'about',
        title: '关于我们',
        subtitle: '版本信息、联系方式',
        type: 'navigate',
        url: '/pages/user-center/about/about'
      },
      {
        id: 'feedback',
        title: '意见反馈',
        subtitle: '问题反馈、建议',
        type: 'navigate',
        url: '/pages/user-center/feedback/feedback'
      }
    ]
  },

  onLoad: function(options) {
    this.getUserInfo();
  },

  onShow: function() {
    this.getUserInfo();
  },

  /**
   * 获取用户信息
   */
  getUserInfo: function() {
    const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo');
    if (userInfo) {
      this.setData({
        userInfo: userInfo
      });
    }
  },

  /**
   * 设置项点击事件
   */
  onSettingItemTap: function(e) {
    const item = e.currentTarget.dataset.item;
    
    if (item.type === 'navigate' && item.url) {
      wx.navigateTo({
        url: item.url
      });
    }
  },

  /**
   * 开关切换事件
   */
  onSwitchChange: function(e) {
    const { id } = e.currentTarget.dataset;
    const value = e.detail.value;
    
    // 更新本地状态
    const settingItems = this.data.settingItems.map(item => {
      if (item.id === id) {
        item.value = value;
      }
      return item;
    });
    
    this.setData({
      settingItems: settingItems
    });
    
    // 保存设置到本地存储
    wx.setStorageSync(`setting_${id}`, value);
    
    // 根据不同设置项处理
    switch (id) {
      case 'notification':
        this.handleNotificationSetting(value);
        break;
      case 'auto-cancel':
        this.handleAutoCancelSetting(value);
        break;
    }
  },

  /**
   * 处理通知设置
   */
  handleNotificationSetting: function(enabled) {
    if (enabled) {
      // 请求订阅消息权限
      wx.requestSubscribeMessage({
        tmplIds: ['预约提醒模板ID', '迟到提醒模板ID'],
        success: (res) => {
          console.log('订阅消息设置成功:', res);
        },
        fail: (err) => {
          console.error('订阅消息设置失败:', err);
        }
      });
    }
  },

  /**
   * 处理自动取消设置
   */
  handleAutoCancelSetting: function(enabled) {
    // 这里可以调用后端接口保存用户偏好设置
    console.log('自动取消预约设置:', enabled);
  },

  /**
   * 清除缓存
   */
  clearCache: function() {
    wx.showModal({
      title: '清除缓存',
      content: '确定要清除所有缓存数据吗？',
      success: (res) => {
        if (res.confirm) {
          try {
            wx.clearStorageSync();
            wx.showToast({
              title: '缓存清除成功',
              icon: 'success'
            });
          } catch (e) {
            wx.showToast({
              title: '清除失败',
              icon: 'error'
            });
          }
        }
      }
    });
  },

  /**
   * 检查更新
   */
  checkUpdate: function() {
    const updateManager = wx.getUpdateManager();
    
    updateManager.onCheckForUpdate(function (res) {
      if (res.hasUpdate) {
        wx.showModal({
          title: '发现新版本',
          content: '发现新版本，是否重启应用？',
          success: function (res) {
            if (res.confirm) {
              updateManager.applyUpdate();
            }
          }
        });
      } else {
        wx.showToast({
          title: '已是最新版本',
          icon: 'success'
        });
      }
    });
    
    updateManager.checkForUpdate();
  }
}); 