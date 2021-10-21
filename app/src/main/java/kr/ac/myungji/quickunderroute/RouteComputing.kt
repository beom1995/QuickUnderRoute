package kr.ac.myungji.quickunderroute

import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

private const val INF: Int = 1000000000     // 값이 무한대(infinity)라 가정

var helper: AppDatabase? = null

class RouteComputing {
    private val edgeList: List<RoomEdge>? = helper?.roomEdgeDao()?.getAll()
    private val stationList: List<RoomStation>? = helper?.roomStationDao()?.getAll()
    private var totalCost: Int = 0
    private lateinit var bestRoute: MutableList<RoomEdge>

    // mode는 시간0, 거리1, 요금2
    fun dijkstra(mode: Int, src: Int, dstn: Int) {
        lateinit var queue: MutableList<RoomEdge>

        val comparator: Comparator<RoomEdge>? by lazy {
            when(mode) {
                1 -> MinEdgeByTime()
                2 -> MinEdgeByDistance()
                3 -> MinEdgeByFare()
                else -> null
            }
        }

        for(n in 0..edgeList?.size!!) {
            if(edgeList[n].src == src){
                queue.add(edgeList[n])
            }
        }

        if(queue.isNotEmpty()){
            Collections.sort(queue, comparator)
            bestRoute.add(queue[0])
        }else{
            // 경로 없음
        }


        for(n in 0..edgeList?.size!!) {
            if(edgeList[n].src == src){
                totalCost += list[].    queue.add(edgeList[n])

        }

        while (queue.isNotEmpty()) {
            val curIndex = queue.peek().index  // 현재 노드 인덱스
            val curDist = queue.peek().dist  // 현재 노드까지의 거리
            queue.poll()

            if (distance[curIndex] < curDist) continue // 탐색 시간을 줄이기 위해
            // 현재 거리가 현재 노드까지의 거리보다 작으면 탐색 중단

            for (i in 0 until stationList[].size) { // 연결된 노드들 탐색
                val nextIndex = arr[curIndex][i].index
                val nextDist = curDist + arr[curIndex][i].dist

                if (nextDist < distance[nextIndex]) {
                    distance[nextIndex] = nextDist
                    queue.add(Node(nextIndex, nextDist))
                }
            }
        }
    }
}