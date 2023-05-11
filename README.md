# ClientForCDS
multi client simulator for cluster data source server. 

***
## command line 명령어
- "get" : getData() (print total request count)
- "connect n" : n clients try connect
- "request n m" : n clients each request m times.
- "affinity n" : 이 명령어 입력 이후로 각 요청마다 요구하는 connection 개수가 n이 됨. (기본으로 돌리고 싶으면 affinity 1 입력)
- "wait n" : 명령어 입력 스레드가 n초만큼 멈춤. (명령어 여러 개 사이에 시간 텀을 두고 싶을 때 사용)