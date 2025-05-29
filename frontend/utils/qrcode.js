/**
 * 简化的QR码生成器
 * 注意：这是一个简化版本，实际生产环境建议使用专业的QR码库
 */

class SimpleQRCode {
  constructor(text, size = 25) {
    this.text = text;
    this.size = size;
    this.modules = [];
    this.init();
  }

  init() {
    // 初始化模块数组
    this.modules = Array(this.size).fill().map(() => Array(this.size).fill(false));
    
    // 生成基础模式
    this.generatePattern();
    
    // 添加定位角
    this.addFinderPatterns();
    
    // 添加时序图案
    this.addTimingPatterns();
    
    // 添加数据模式（简化版）
    this.addDataPattern();
  }

  generatePattern() {
    // 基于文本内容生成简单的模式
    const hash = this.stringToHash(this.text);
    
    for (let i = 0; i < this.size; i++) {
      for (let j = 0; j < this.size; j++) {
        // 跳过功能区域
        if (this.isReservedArea(i, j)) continue;
        
        // 基于哈希值生成模式
        const value = (hash + i * 7 + j * 13 + i * j) % 256;
        this.modules[i][j] = value % 2 === 0;
      }
    }
  }

  addFinderPatterns() {
    // 左上角
    this.addFinderPattern(0, 0);
    // 右上角
    this.addFinderPattern(0, this.size - 7);
    // 左下角
    this.addFinderPattern(this.size - 7, 0);
  }

  addFinderPattern(row, col) {
    const pattern = [
      [1,1,1,1,1,1,1],
      [1,0,0,0,0,0,1],
      [1,0,1,1,1,0,1],
      [1,0,1,1,1,0,1],
      [1,0,1,1,1,0,1],
      [1,0,0,0,0,0,1],
      [1,1,1,1,1,1,1]
    ];

    for (let i = 0; i < 7; i++) {
      for (let j = 0; j < 7; j++) {
        if (row + i < this.size && col + j < this.size) {
          this.modules[row + i][col + j] = pattern[i][j] === 1;
        }
      }
    }

    // 添加白色边框
    for (let i = -1; i <= 7; i++) {
      for (let j = -1; j <= 7; j++) {
        if (row + i >= 0 && row + i < this.size && 
            col + j >= 0 && col + j < this.size &&
            (i === -1 || i === 7 || j === -1 || j === 7)) {
          this.modules[row + i][col + j] = false;
        }
      }
    }
  }

  addTimingPatterns() {
    // 水平时序图案
    for (let i = 8; i < this.size - 8; i++) {
      this.modules[6][i] = i % 2 === 0;
    }
    
    // 垂直时序图案
    for (let i = 8; i < this.size - 8; i++) {
      this.modules[i][6] = i % 2 === 0;
    }
  }

  addDataPattern() {
    const hash = this.stringToHash(this.text);
    
    // 在非保留区域添加数据模式
    for (let i = 0; i < this.size; i++) {
      for (let j = 0; j < this.size; j++) {
        if (!this.isReservedArea(i, j)) {
          const value = (hash + i * 3 + j * 5) % 100;
          this.modules[i][j] = value < 50;
        }
      }
    }
  }

  isReservedArea(row, col) {
    // 定位角区域
    if ((row < 9 && col < 9) || 
        (row < 9 && col >= this.size - 8) || 
        (row >= this.size - 8 && col < 9)) {
      return true;
    }
    
    // 时序图案
    if (row === 6 || col === 6) {
      return true;
    }
    
    return false;
  }

  stringToHash(str) {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // 转换为32位整数
    }
    return Math.abs(hash);
  }

  getModules() {
    return this.modules;
  }
}

/**
 * 在Canvas上绘制QR码
 * @param {CanvasRenderingContext2D} ctx Canvas上下文
 * @param {string} text 要编码的文本
 * @param {number} canvasSize Canvas尺寸
 * @param {number} moduleSize QR码模块大小，默认25
 */
function drawQRCodeOnCanvas(ctx, text, canvasSize, moduleSize = 25) {
  const qr = new SimpleQRCode(text, moduleSize);
  const modules = qr.getModules();
  const cellSize = canvasSize / moduleSize;
  
  // 清空画布
  ctx.clearRect(0, 0, canvasSize, canvasSize);
  
  // 绘制白色背景
  ctx.fillStyle = '#ffffff';
  ctx.fillRect(0, 0, canvasSize, canvasSize);
  
  // 绘制QR码模块
  ctx.fillStyle = '#000000';
  
  for (let row = 0; row < moduleSize; row++) {
    for (let col = 0; col < moduleSize; col++) {
      if (modules[row][col]) {
        ctx.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
      }
    }
  }
}

/**
 * 生成QR码数据URL
 * @param {string} text 要编码的文本
 * @param {number} size 图片尺寸
 * @param {number} moduleSize QR码模块大小
 * @returns {string} 数据URL
 */
function generateQRCodeDataURL(text, size = 500, moduleSize = 25) {
  // 创建离屏canvas
  const canvas = wx.createOffscreenCanvas();
  canvas.width = size;
  canvas.height = size;
  const ctx = canvas.getContext('2d');
  
  // 绘制QR码
  drawQRCodeOnCanvas(ctx, text, size, moduleSize);
  
  // 返回数据URL
  return canvas.toDataURL();
}

module.exports = {
  SimpleQRCode,
  drawQRCodeOnCanvas,
  generateQRCodeDataURL
}; 