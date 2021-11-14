package kr.ac.myungji.quickunderroute

import android.util.Log
import kr.ac.myungji.quickunderroute.entity.RoomEdge
import java.util.*

private const val INF: Int = 1000000000     // 값이 무한대(infinity)라 가정

class RouteComputing(db: AppDatabase?) {
    private val edgeList: List<RoomEdge>? = db!!.roomEdgeDao().getAll()     // 모든 edge
    private lateinit var cost: IntArray

//    // 최종 최적경로
//    private lateinit var minTime: MutableList<RoomEdge>
//    private lateinit var minDist: MutableList<RoomEdge>
//    private lateinit var minFare: MutableList<RoomEdge>

    private lateinit var vis: BooleanArray

    // minheap을 이용하여 가장 적은 비용을 가진 경로를 계산
    private val queueTime = PriorityQueue<Node>()
    private val queueDist = PriorityQueue<Node>()
    private val queueFare = PriorityQueue<Node>()

    // 최소비용
    private var minTime: Int = 0
    private var minDist: Int = 0
    private var minFare: Int = 0

    fun dijkstra(src: Int, via: Int?, dstn: Int) {
        var curSrc = src



        vis = BooleanArray(111)

        queueTime.add(Node(curSrc, 0))
        queueDist.add(Node(curSrc, 0))
        queueFare.add(Node(curSrc, 0))

        cost = IntArray(910) { INF }
        cost[curSrc] = 0 // 시작 거리는 0

        while (queueTime.isNotEmpty()) {
            curSrc = queueTime.peek().index  // 현재 노드 인덱스
            val curCost = queueTime.peek().cost  // 현재 노드까지의 거리
            queueTime.poll()

            if (cost[curSrc] < curCost) continue // 탐색 시간을 줄이기 위해
            // 현재 거리가 현재 노드까지의 거리보다 작으면 탐색 중단

            if (edgeList != null) {
                for (i in edgeList.indices) { // 연결된 노드들 탐색
                    //최단시간
                    if(edgeList[i].src == curSrc) {
                        val nextIndex = edgeList[i].dstn
                        val nextCost = curCost + edgeList[i].timeSec

                        if (nextCost < cost[nextIndex]) {
                            cost[nextIndex] = nextCost
                            queueTime.add(Node(nextIndex, nextCost))
                        }
                    }
                }
            }
        }
        Log.d("dijstra time result", cost[dstn].toString())
        minTime = cost[dstn]

        cost = IntArray(910) { INF }
        cost[curSrc] = 0 // 시작 거리는 0
        while (queueDist.isNotEmpty()) {
            curSrc = queueDist.peek().index  // 현재 노드 인덱스
            val curCost = queueDist.peek().cost  // 현재 노드까지의 거리
            queueDist.poll()

            if (cost[curSrc] < curCost) continue // 탐색 시간을 줄이기 위해
            // 현재 거리가 현재 노드까지의 거리보다 작으면 탐색 중단

            if (edgeList != null) {
                for (i in edgeList.indices) { // 연결된 노드들 탐색
                    //최단시간
                    if(edgeList[i].src == curSrc) {
                        val nextIndex = edgeList[i].dstn
                        val nextCost = curCost + edgeList[i].distanceM

                        if (nextCost < cost[nextIndex]) {
                            cost[nextIndex] = nextCost
                            queueDist.add(Node(nextIndex, nextCost))
                        }
                    }
                }
            }
        }
        Log.d("dijstra dist result", cost[dstn].toString())
        minDist = cost[dstn]

        cost = IntArray(910) { INF }
        cost[curSrc] = 0 // 시작 거리는 0
        while (queueFare.isNotEmpty()) {
            curSrc = queueFare.peek().index  // 현재 노드 인덱스
            val curCost = queueFare.peek().cost  // 현재 노드까지의 거리
            queueFare.poll()

            if (cost[curSrc] < curCost) continue // 탐색 시간을 줄이기 위해
            // 현재 거리가 현재 노드까지의 거리보다 작으면 탐색 중단

            if (edgeList != null) {
                for (i in edgeList.indices) { // 연결된 노드들 탐색
                    //최단시간
                    if (edgeList[i].src == curSrc) {
                        val nextIndex = edgeList[i].dstn
                        val nextCost = curCost + edgeList[i].fareWon

                        if (nextCost < cost[nextIndex]) {
                            cost[nextIndex] = nextCost
                            queueFare.add(Node(nextIndex, nextCost))
                        }
                    }
                }
            }
        }
        Log.d("dijstra fare result", cost[dstn].toString())
        minFare = cost[dstn]

    }
}


data class Node(val index: Int, val cost: Int) : Comparable<Node> {
    override fun compareTo(other: Node): Int = cost-other.cost
}