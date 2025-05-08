// seat-reservation.js
Page({
  data: {
    roomId: null
  },
  
  onLoad: function(options) {
    // 获取页面参数
    if (options.roomId) {
      this.setData({
        roomId: options.roomId
      });
    }
  }
}) 