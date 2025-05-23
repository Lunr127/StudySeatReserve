const app = getApp();
const { favoriteApi } = require('../../../utils/api');

Page({
  data: {
    favoriteList: [],
    loading: true,
    isEmpty: false
  },

  onLoad: function(options) {
    this.loadFavorites();
  },

  onShow: function() {
    // 页面显示时刷新收藏列表
    this.loadFavorites();
  },

  /**
   * 加载收藏列表
   */
  loadFavorites: function() {
    this.setData({
      loading: true
    });

    favoriteApi.getMyFavorites().then(res => {
      if (res.code === 200) {
        const favoriteList = res.data || [];
        this.setData({
          favoriteList: favoriteList,
          isEmpty: favoriteList.length === 0,
          loading: false
        });
      } else {
        wx.showToast({
          title: res.message || '获取收藏失败',
          icon: 'none'
        });
        this.setData({
          loading: false,
          isEmpty: true
        });
      }
    }).catch(err => {
      console.error('获取收藏错误:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
      this.setData({
        loading: false,
        isEmpty: true
      });
    });
  },

  /**
   * 取消收藏
   */
  removeFavorite: function(e) {
    const { id, title } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '取消收藏',
      content: `确定要取消收藏"${title}"吗？`,
      success: (res) => {
        if (res.confirm) {
          this.doRemoveFavorite(id);
        }
      }
    });
  },

  /**
   * 执行取消收藏
   */
  doRemoveFavorite: function(id) {
    wx.showLoading({
      title: '处理中...',
    });

    favoriteApi.deleteFavorite(id).then(res => {
      wx.hideLoading();
      
      if (res.code === 200) {
        wx.showToast({
          title: '取消收藏成功',
          icon: 'success'
        });
        // 刷新列表
        this.loadFavorites();
      } else {
        wx.showToast({
          title: res.message || '取消收藏失败',
          icon: 'none'
        });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('取消收藏错误:', err);
      wx.showToast({
        title: '网络错误',
        icon: 'error'
      });
    });
  },

  /**
   * 查看收藏项详情
   */
  viewFavoriteDetail: function(e) {
    const { favoriteType, studyRoomId, seatId } = e.currentTarget.dataset;
    
    if (favoriteType === 1) {
      // 收藏的是自习室，跳转到自习室详情页
      wx.navigateTo({
        url: `/pages/study-rooms/detail/detail?id=${studyRoomId}`
      });
    } else if (favoriteType === 2) {
      // 收藏的是座位，跳转到座位选择页
      wx.navigateTo({
        url: `/pages/seat-reservation/seat-selection/seat-selection?roomId=${studyRoomId}&seatId=${seatId}`
      });
    }
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh: function() {
    this.loadFavorites();
    wx.stopPullDownRefresh();
  },

  /**
   * 跳转到自习室列表
   */
  goToStudyRooms: function() {
    wx.switchTab({
      url: '/pages/study-rooms/study-rooms'
    });
  }
}); 