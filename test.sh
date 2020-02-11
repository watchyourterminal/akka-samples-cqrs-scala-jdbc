#!/bin/bash

# for i in {1..50};
# do
#     for port in {1..3}; do
# 	curl -X PUT -H "Content-Type: application/json" -d "{\"cartId\":\"alska\", \"itemId\":\"Hits stick\", \"quantity\":$i}" http://127.0.0.1:805$port/shopping/carts &
#     done
# done

# echo "Done!"
# curl http://127.0.0.1:8051/shopping/carts/alska


for i in {1..1000}; do
    curl http://127.0.0.1:8053/shopping/carts/$i &
done
