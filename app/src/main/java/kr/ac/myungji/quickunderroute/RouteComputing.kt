package kr.ac.myungji.quickunderroute

import java.util.*
import kotlin.collections.ArrayList

private lateinit var arr: ArrayList<ArrayList<Node>>
private lateinit var distance: IntArray
private lateinit var vis: BooleanArray
private val queue = PriorityQueue<Node>()
private const val INF = 1000000000

var helper: AppDatabase? = null

class RouteComputing {

//    private lateinit var st: StringTokenizer

//    private val edge: ArrayList<RoomEdge>() = helper?.RoomEdgeDAO()?.getAll()?
 //   private val station?: ArrayList<RoomStation>() = null

    private fun main() {
//        val br = BufferedReader(InputStreamReader(System.`in`))
//        val bw = BufferedWriter(OutputStreamWriter(System.out))

//        st = StringTokenizer(br.readLine())
//        val n = st.nextToken().toInt()
//        val e = st.nextToken().toInt()

//        arr = ArrayList()
//        for (i in 0 until n) arr.add(ArrayList())
/*
        distance = IntArray(n) { INF }
        vis = BooleanArray(n)
*/
//        st = StringTokenizer(br.readLine())
//        val k = st.nextToken().toInt() - 1
/*
        for (i in 0 until e) {
            st = StringTokenizer(br.readLine())
            val u = st.nextToken().toInt()-1
            val v = st.nextToken().toInt()-1
            val w = st.nextToken().toInt()
            arr[u].add(Node(v, w ))
        }
*/
//        dijkstra(k)
/*
        distance.forEach {
            if (it == INF) {
                bw.write("INF")
                bw.write("\n")
            } else {
                bw.write(it.toString())
                bw.write("\n")
            }
        }
        bw.flush()
        bw.close()

 */
    }

    private fun dijkstra(start: Int) {
        distance[start] = 0 // 시작 거리는 0
        queue.add(Node(start, 0)) // 시작 노드를 큐에 넣어줍니다

        while (queue.isNotEmpty()) {
            val curIndex = queue.peek().index  // 현재 노드 인덱스
            val curDist = queue.peek().dist  // 현재 노드까지의 거리
            queue.poll()

            if (distance[curIndex] < curDist) continue // 탐색 시간을 줄이기 위해
            // 현재 거리가 현재 노드까지의 거리보다 작으면 탐색 중단

            for (i in 0 until arr[curIndex].size) { // 연결된 노드들 탐색
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

data class Node(val index: Int, val dist: Int) : Comparable<Node> {
    override fun compareTo(other: Node): Int = dist-other.dist
}