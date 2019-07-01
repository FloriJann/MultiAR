package inovex.ad.multiar.arViewerModule

import android.view.MotionEvent
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem


class AdvancedTransformableNode(ts: TransformationSystem, private val onTabSelectListener: OnTabSelectListener) :
    TransformableNode(ts) {


    interface OnTabSelectListener {
        fun onTabSelect(transformableNode: TransformableNode)
    }

    var lockOnCam:Boolean
        get() = lockOnCam
        set(value) {
            if(value) {
                // save start rotation
                startRot =  worldRotation
                isLocked = true
            } else {
                isLocked = false
                worldRotation = startRot
            }
        }

    private var startRot = worldRotation
    private var isLocked = false

    private val rotationSpeed = 1f

    private val interactionRangeCam = 1.1f

    private var offsetVector = Vector3(0f, 0f, 0f)

    override fun onUpdate(frameTime: FrameTime) {
        if(isLocked) {
            lookAtCamera()
        } else if (cameraInRange()) {
            rotateAroundY()
        }
    }

    fun setOffset(offset: Float) {
        offsetVector.y = offset
        worldPosition = Vector3.add(parent.worldPosition, offsetVector)
    }


    private fun cameraInRange(): Boolean {
        return (worldPosition.distance(scene.camera.worldPosition) <= interactionRangeCam)
    }

    private fun lookAtCamera() {
        val cameraPosition = scene.camera.worldPosition
        val direction = Vector3.subtract(worldPosition,cameraPosition )
        val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
        worldRotation = lookRotation
    }

    private fun rotateAroundY() {
        val rotation = Quaternion.axisAngle(Vector3.up(), rotationSpeed)
        worldRotation = Quaternion.multiply(worldRotation, rotation)
    }

    override fun onTap(hitTestResult: HitTestResult?, motionEvent: MotionEvent?) {
        super.onTap(hitTestResult, motionEvent)
        onTabSelectListener.onTabSelect(this)
    }

    fun Vector3.distance(v2: Vector3): Float {

        val x = (v2.x - this.x) * (v2.x - this.x)
        val y = (v2.y - this.y) * (v2.y - this.y)
        val z = (v2.z - this.z) * (v2.z - this.z)
        return Math.sqrt((x + y + z).toDouble()).toFloat()
    }
}