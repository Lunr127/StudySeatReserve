# 使用nginx作为基础镜像
FROM nginx:alpine

# 设置工作目录
WORKDIR /usr/share/nginx/html

# 复制小程序源文件
COPY . .

# 复制nginx配置文件
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"] 