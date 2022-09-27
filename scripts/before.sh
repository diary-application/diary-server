#!/usr/bin/env bash

# 기존 프로젝트 폴더가 존재하면 삭제하고 다시 다운로드
if [ -d /home/ubuntu/diary-server/ ]; then
  rm -rf /home/ubuntu/diary-server/
fi
mkdir -vp /home/ubuntu/diary-server/
sudo cp /home/ubuntu/application-db.yml /home/ubuntu/diary-server/src/main/resources/application-db.yml