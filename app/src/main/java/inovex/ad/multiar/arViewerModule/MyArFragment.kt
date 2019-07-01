package inovex.ad.multiar.arViewerModule


import android.arch.lifecycle.Observer
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import inovex.ad.multiar.MainActivity
import inovex.ad.multiar.walletModule.WalletViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel


/**
 * A simple [ArFragment] subclass.
 *
 */
class MyArFragment : ArFragment(), AdvancedTransformableNode.OnTabSelectListener {


    private val TAG = javaClass.name

    private val walletViewModel: WalletViewModel by sharedViewModel()

    private lateinit var mainActivity: MainActivity


    /*
     *  stored here because a ViewModel must never hold any class that holds a reference to the activity context
     *  (https://developer.android.com/topic/libraries/architecture/viewmodel)!
     *  ModelRenderable does hold a reference.
     */
    private val modelBlueprintsMap = mutableMapOf<Int, ModelRenderable>()

    private var selectedNode: TransformableNode? = null

    private val nodesList = mutableListOf<AdvancedTransformableNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        walletViewModel.isArActive = true       // to disable the remove button and to enable selection in wallet

        this.setOnTapArPlaneListener { hitResult: HitResult, _, _ ->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(this.arSceneView.scene)

            placeAndSelectModel(anchorNode)
        }
    }

    override fun onStart() {
        super.onStart()

        walletViewModel.entries.observe(this, Observer {
            modelBlueprintsMap.clear()
            var mapKey = 0
            it?.forEach { walletEntry ->
                //Log.d(TAG, "walletEntry = " + walletEntry.displayName)
                makeModelFromUrl(mapKey, walletEntry.modelURL)
                mapKey++
            }
        })
    }


    override fun onPause() {
        super.onPause()

        mainActivity.removeArOverlayFragment()
        mainActivity.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        walletViewModel.isArActive = false
    }

    private fun makeModelFromUrl(key: Int, path: String) {
        val uri = Uri.parse(path)
        //Log.d(TAG, "modelUri = $uri")

        ModelRenderable.builder()
            .setSource(
                this.context, RenderableSource.builder().setSource(
                    this.context,
                    uri,
                    RenderableSource.SourceType.GLTF2
                ).build()
            )
            .setRegistryId(uri)
            .build()
            .thenAccept {
                val model = it
                if (model != null) {
                    modelBlueprintsMap[key] = model
                }
            }
            .exceptionally {
                Log.e(TAG, it.message)

                val toast = Toast.makeText(this.context, "Unable to load renderable", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                null
            }
    }

    private fun placeAndSelectModel(anchorNode: AnchorNode) {
        val transformableNode = AdvancedTransformableNode(this.transformationSystem, this)

        transformableNode.renderable = modelBlueprintsMap[walletViewModel.currentEntry]
        transformableNode.resize()
        //transformableNode.addBoundingBox(this.context as Context)
        transformableNode.setParent(anchorNode)

        nodesList.add(transformableNode)

        transformableNode.select()
        onTabSelect(transformableNode)
    }


    override fun onTabSelect(transformableNode: TransformableNode) {
        selectedNode = transformableNode
        mainActivity.setArOverlayVisible(true)
    }

    fun deleteSelectedNode() {
        if (selectedNode != null) {
            nodesList.remove(selectedNode)

            (selectedNode?.parent as AnchorNode).anchor.detach()
            mainActivity.setArOverlayVisible(false)
        }

    }

    fun offsetChanged(progress: Int) {
        val offset = progress / 100.0f

        (selectedNode as AdvancedTransformableNode).setOffset(offset)
    }

    fun lookSwitchSet(value: Boolean) {
        //Log.d(TAG,"lookSwitchSet to $value")

        nodesList.forEach {
            it.lockOnCam = value
        }
    }


    private fun TransformableNode.resize() {
        val startBox = (this.renderable.collisionShape as Box)

        val maxDimension = floatArrayOf(
            startBox.size.x,
            startBox.size.y,
            startBox.size.z
        ).max() as Float

        val scaleFactor = 1.0f / maxDimension

        this.scaleController.minScale = scaleFactor * 0.5f
        this.scaleController.maxScale = scaleFactor * 2.0f

        this.localScale = Vector3(scaleFactor, scaleFactor, scaleFactor)
    }


    /*
            Used for debugging
            Creates a node to display the bounds of the object
    */
    private fun Node.addBoundingBox(context: Context) {
        val boundsNode = Node()
        boundsNode.setParent(this)
        MaterialFactory.makeTransparentWithColor(context, Color(1.0f, 0.0f, 0.0f, 0.5f))
            .thenAccept { material ->
                val box = this.collisionShape as Box
                val renderable = ShapeFactory.makeCube(box.size, box.center, material)
                renderable.collisionShape = null
                boundsNode.renderable = renderable
            }
    }

}
