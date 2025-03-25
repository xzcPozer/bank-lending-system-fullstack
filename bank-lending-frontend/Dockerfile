# Stage 1: Сборка приложения
FROM node:22-alpine AS builder

# Установка рабочей директории
WORKDIR /app

# Копирование package.json и package-lock.json
COPY package*.json ./

# Установка зависимостей
RUN npm install

# Копирование исходного кода приложения
COPY . .

# Сборка приложения в production режиме
RUN npm run build -- --configuration production

# Stage 2: Окончательный образ для запуска приложения
FROM nginx:alpine

# Копирование конфигурации Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Копирование собранного приложения из первого этапа в директорию Nginx
COPY --from=builder /app/dist/bank-lending-frontend /usr/share/nginx/html

# Открытие порта 80 для HTTP
EXPOSE 80

# Запуск Nginx
CMD ["nginx", "-g", "daemon off;"]
