const app = getApp();
const { userApi } = require('../../../utils/api');

Page({
  data: {
    userInfo: {},
    settings: {
      enableNotification: true,
      enableAutoCancel: false,
      autoCancelMinutes: 15
    },
    loading: true,
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
    this.loadUserInfo();
    this.loadUserPreferences();
  },

  onShow: function() {
    // 页面显示时刷新用户信息
    this.loadUserInfo();
  },

  /**
   * 加载用户信息
   */
  loadUserInfo: function() {
    const userInfo = app.globalData.userInfo;
    if (userInfo) {
      this.setData({
        userInfo: userInfo
      });
    }
  },

  /**
   * 加载用户偏好设置
   */
  loadUserPreferences: function() {
    userApi.getUserPreferences().then(res => {
      if (res.code === 200) {
        const preferences = res.data;
        this.setData({
          settings: {
            enableNotification: preferences.enableNotification !== false,
            enableAutoCancel: preferences.enableAutoCancel === true,
            autoCancelMinutes: preferences.autoCancelMinutes || 15
          },
          loading: false
        });
      } else {
        console.error('获取用户偏好设置失败:', res.message);
        this.setData({
          loading: false
        });
      }
    }).catch(err => {
      console.error('获取用户偏好设置错误:', err);
      this.setData({
        loading: false
      });
    });
  },

  /**
   * 切换通知设置
   */
  toggleNotification: function(e) {
    const enabled = e.detail.value;
    
    if (enabled) {
      // 如果要启用通知，先请求订阅消息权限
      this.requestSubscribeMessage(() => {
        this.updateSetting('enableNotification', enabled);
      });
    } else {
      this.updateSetting('enableNotification', enabled);
    }
  },

  /**
   * 切换自动取消设置
   */
  toggleAutoCancel: function(e) {
    const enabled = e.detail.value;
    this.updateSetting('enableAutoCancel', enabled);
  },

  /**
   * 更新设置项
   */
  updateSetting: function(key, value) {
    // 更新本地数据
    this.setData({
      [`settings.${key}`]: value
    });

    // 构建更新数据
    const updateData = {};
    updateData[key] = value;

    // 调用API更新
    userApi.updateUserPreferences(updateData).then(res => {
      if (res.code === 200) {
        wx.showToast({
          title: '设置已保存',
          icon: 'success'
        });
      } else {
        wx.showToast({
          title: res.message || '保存失败',
          icon: 'none'
        });
        // 恢复原值
        this.loadUserPreferences();
      }
    }).catch(err => {
      console.error('更新设置失败:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
      // 恢复原值
      this.loadUserPreferences();
    });
  },

  /**
   * 请求订阅消息权限
   */
  requestSubscribeMessage: function(callback) {
    wx.requestSubscribeMessage({
      tmplIds: ['template_id_for_reservation', 'template_id_for_reminder'], // 替换为实际的模板ID
      success: (res) => {
        console.log('订阅消息授权结果:', res);
        if (callback) callback();
      },
      fail: (err) => {
        console.error('订阅消息授权失败:', err);
        wx.showToast({
          title: '需要授权才能接收通知',
          icon: 'none'
        });
      }
    });
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
              title: '缓存已清除',
              icon: 'success'
            });
          } catch (err) {
            console.error('清除缓存失败:', err);
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
    wx.showLoading({
      title: '检查中...'
    });

    // 模拟检查更新
    setTimeout(() => {
      wx.hideLoading();
      wx.showToast({
        title: '已是最新版本',
        icon: 'success'
      });
    }, 1500);
  },

  /**
   * 关于我们
   */
  aboutUs: function() {
    wx.navigateTo({
      url: '/pages/about/about'
    });
  },

  /**
   * 意见反馈
   */
  feedback: function() {
    wx.navigateTo({
      url: '/pages/feedback/feedback'
    });
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
}); 