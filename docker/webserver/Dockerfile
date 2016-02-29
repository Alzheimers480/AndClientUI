FROM php:5.6-apache
MAINTAINER David Keller <dnkeller@oakland.edu>
RUN docker-php-ext-install mysqli
COPY config/php.ini /usr/local/etc/php/
COPY src/ /var/www/html/
RUN apt-get update && \
  apt-get install -y ssmtp && \
  apt-get clean && \
  echo "FromLineOverride=YES" >> /etc/ssmtp/ssmtp.conf && \
  echo 'sendmail_path = "/usr/sbin/ssmtp -t"' > /usr/local/etc/php/conf.d/mail.ini
