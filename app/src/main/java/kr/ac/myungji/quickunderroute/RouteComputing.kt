package kr.ac.myungji.quickunderroute

import android.util.Log
import androidx.room.Room
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

private const val INF: Int = 1000000000     // 값이 무한대(infinity)라 가정

class RouteComputing {
    var helper: AppDatabase? = null

    private val edgeList: List<RoomEdge>? = helper?.roomEdgeDao()?.getAll()     // 모든 edge
    private val stationList: List<RoomStation>? = helper?.roomStationDao()?.getAll()     // 모든 station
    private var totalCost: Int = 0
    private lateinit var cost: IntArray

    // 최종 최적경로
    private lateinit var minTime: MutableList<RoomEdge>
    private lateinit var minDist: MutableList<RoomEdge>
    private lateinit var minFare: MutableList<RoomEdge>

    // 계산 시 사용
    private val heap = PriorityQueue<RoomEdge>()

    private lateinit var arr: ArrayList<ArrayList<Node>>
    private lateinit var dist: IntArray
    private lateinit var vis: BooleanArray
    private val queue = PriorityQueue<Node>()


    fun dijkstra(src: Int, via: Int?, dstn: Int) {
 //       lateinit var queue: Array<Array<Int>>
        var curSrc = src

//        for (i in 0..111) arr.add(ArrayList())
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
            Log.d("dijstra", cost[dstn].toString())
        }
    }
}
data class Node(val index: Int, val cost: Int) : Comparable<Node> {
    override fun compareTo(other: Node): Int = cost-other.cost
}