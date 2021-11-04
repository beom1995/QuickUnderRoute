package kr.ac.myungji.quickunderroute

import android.util.Log
import kr.ac.myungji.quickunderroute.entity.RoomEdge
import java.util.*

private const val INF: Int = 1000000000     // 값이 무한대(infinity)라 가정

class RouteComputing(db: AppDatabase?) {
    private val edgeList: List<RoomEdge>? = db!!.roomEdgeDao().getAll()     // 모든 edge
    private lateinit var cost: IntArray

    // 최종 최적경로
    private lateinit var minTime: MutableList<RoomEdge>
    private lateinit var minDist: MutableList<RoomEdge>
    private lateinit var minFare: MutableList<RoomEdge>

    // 계산 시 사용
    private val heap = PriorityQueue<RoomEdge>()

    private lateinit var vis: BooleanArray
    private val queue = PriorityQueue<Node>()   // minheap을 이용

    fun dijkstra(src: Int, via: Int?, dstn: Int) {
        var curSrc = src

        cost = IntArray(910) { INF }
        vis = BooleanArray(111)

        cost[curSrc] = 0 // 시작 거리는 0
        queue.add(Node(curSrc, 0))

        while (queue.isNotEmpty()) {
            curSrc = queue.peek().index  // 현재 노드 인덱스
            val curCost = queue.peek().cost  // 현재 노드까지의 거리
            queue.poll()

            if (cost[curSrc] < curCost) continue // 탐색 시간을 줄이기 위해
            // 현재 거리가 현재 노드까지의 거리보다 작으면 탐색 중단

            if (edgeList != null) {
                for (i in edgeList.indices) { // 연결된 노드들 탐색
                    if(edgeList[i].src == curSrc) {
                        val nextIndex = edgeList[i].dstn
                        val nextCost = curCost + edgeList[i].timeSec

                        if (nextCost < cost[nextIndex]) {
                            cost[nextIndex] = nextCost
                            queue.add(Node(nextIndex, nextCost))
                        }
                    }
                }
            }
            Log.d("dijstra result", cost[dstn].toString())
        }
    }
}


data class Node(val index: Int, val cost: Int) : Comparable<Node> {
    override fun compareTo(other: Node): Int = cost-other.cost
}