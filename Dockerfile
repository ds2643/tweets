FROM clojure
LABEL maintainer="Divyum Rastogi"
COPY . /usr/src/app
WORKDIR /usr/src/app                                 
EXPOSE 3666
CMD ["lein", "run"]