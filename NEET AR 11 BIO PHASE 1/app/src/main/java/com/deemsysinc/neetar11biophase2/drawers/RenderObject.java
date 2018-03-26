package com.deemsysinc.neetar11biophase2.drawers;

import android.content.Context;
import android.util.Log;


import com.deemsysinc.neetar11biophase2.expansion.APKExpansionSupport;
import com.deemsysinc.neetar11biophase2.expansion.MyUtils;
import com.deemsysinc.neetar11biophase2.expansion.ZipResourceFile;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CRC32;

import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjSplitting;
import de.javagl.obj.ObjUtils;


public class RenderObject
{
    String theMaterialname;
    private ZipResourceFile expansionFile = null;

    MyUtils utils = new MyUtils();

    InputStream modelStram,mtlStream;

    int noOfVertices=0;


    private static final String TAG =
            RenderObject.class.getSimpleName();

    /**
     * The list of ObjectRenderer instances that will render the individual
     * parts
     */
    private final List<ObjectRenderer> materialGroupObjectRenderers;


    private final List<ObjectRenderer> materialGroupObjectRenders1;

    /**
     * Default constructor
     */


    public RenderObject()
    {
        this.materialGroupObjectRenderers = new ArrayList<>();
        this.materialGroupObjectRenders1 = new ArrayList<>();
    }


    public void createOnGlThread(Boolean isMounted,String obbpath,Context context, String objpath,String mtlPath,
                                 String defaultTextureFileName,boolean isPartsAvaliable,String parts) throws IOException
    {

        // Read the obj file.
        Log.d("ObjPathPlus",objpath+","+mtlPath);




        Log.d("PrintObbPath",obbpath);

        try {

            if(isMounted)
            {
                initializePath(context,obbpath,objpath,mtlPath);
            }
            Log.d("CalledWho","createOnGlThread");
            Log.d("GetInputStream",""+modelStram);

            Obj obj = ObjReader.read(modelStram);


            // Prepare the Obj so that its structure is suitable for
            // rendering with OpenGL:
            // 1. Triangulate it
            // 2. Make sure that texture coordinates are not ambiguous
            // 3. Make sure that normals are not ambiguous
            // 4. Convert it to single-indexed data
            Obj renderableObj = ObjUtils.convertToRenderable(obj);

            // When there are no material groups, then just render the object
            // using the default texture
            Log.d("HowManyVertices", "" + renderableObj.getNumVertices());
            Log.d("GetTMatG", "" + renderableObj.getNumMaterialGroups());
            if (renderableObj.getNumMaterialGroups() == 0) {
                createRenderers(context, renderableObj,expansionFile, null,isPartsAvaliable,"","");
            } else {
                // Otherwise, create one renderer for each material
                createMaterialBasedRenderers(context, renderableObj,
                        defaultTextureFileName,"",mtlStream,isPartsAvaliable,parts);
            }
        }catch (Exception e)
        {
            Log.d("GetPatheE",""+e.toString());
        }
    }

    private void createRenderers(Context context, Obj obj,ZipResourceFile expansionFile, String textureName,boolean isPartsAvalible,String materialname,String mtlFileName) throws IOException
    {

        noOfVertices=obj.getNumVertices();
        if (obj.getNumVertices() <= 65000)
        {
            createRenderer(context, obj,expansionFile, textureName,isPartsAvalible,materialname,mtlFileName);
        }
        else
        {
            Log.d("ObjectVertextG","Greater");
            // If there are more than 65k vertices, then the object has to be
            // split into multiple parts, each having at most 65k vertices
            List<Obj> objParts = ObjSplitting.splitByMaxNumVertices(obj, 65000);
            for (int j = 0; j < objParts.size(); j++)
            {
                Obj objPart = objParts.get(j);
                createRenderer(context, objPart,expansionFile, textureName,isPartsAvalible,materialname,mtlFileName);
            }
        }
    }

    private void createMaterialBasedRenderers(Context context, Obj obj,
                                              String defaultTextureFileName,String objpath,InputStream mtlStram,boolean isPartsAvalible,String foldername) throws IOException
    {

        // Read the MTL files that are referred to from the OBJ, and
        // extract all their MTL definitions

        List<String> mtlFileNames = obj.getMtlFileNames();
        List<Mtl> allMtls = new ArrayList<>();
        String mtlFilename="";
        for (String mtlFileName : mtlFileNames)
        {
            mtlFilename=mtlFileName;
            Log.d("MtlFileNames",mtlFileName);
            List<Mtl> mtls = MtlReader.read(mtlStram);
            allMtls.addAll(mtls);
        }
        // Obtain the material groups from the OBJ, and create renderers for
        // each of them
        Map<String, Obj> materialGroupObjs =
                ObjSplitting.splitByMaterialGroups(obj);
        for (Map.Entry<String, Obj> entry : materialGroupObjs.entrySet())
        {
            Log.d("PrintMtlFileName",mtlFilename);
            String materialName = entry.getKey();
            Log.d("PrintMaterialName",materialName);
            String textureName=findObbTextureName(materialName,allMtls,defaultTextureFileName,foldername);
            Log.d("PrintTextureStream",""+textureName);
            Obj materialGroupObj = entry.getValue();
            createRenderers(context, materialGroupObj,expansionFile, textureName,isPartsAvalible,materialName,mtlFilename);
        }
    }



    private String findObbTextureName(String materialName, Iterable<? extends Mtl> mtls, String defaultTextureFileName,String foldername)
    {
        for(Mtl mtl:mtls)
        {
            if(Objects.equals(materialName,mtl.getName()))
            {
                ZipResourceFile.ZipEntryRO[] entriesarray = expansionFile.getAllEntries();
                for(ZipResourceFile.ZipEntryRO zipEntryRO:entriesarray)
                {
                    String theFileName=zipEntryRO.mFileName.substring(40,zipEntryRO.mFileName.length());
                    Log.d("PrintTheFileName",theFileName);
                    Log.d("PrintAppendName",foldername+"/"+mtl.getMapKd());
                    if(theFileName.equals(foldername+"/"+mtl.getMapKd()))
                    {
                        Log.d("PrintFolderName",foldername);
                        Log.d("PrintFilterMFile",zipEntryRO.mFileName.substring(40,zipEntryRO.mFileName.length()));
                        return zipEntryRO.mFileName;
                    }

                }

            }
        }
        return defaultTextureFileName;
    }


    private void createRenderer(Context context, Obj obj,ZipResourceFile expansionFile,
                                String textureName,boolean isPartsAvalible,String materialname,String mtlFileName) throws IOException
    {
        Log.d("PrintTextureStream",""+textureName);
        Log.i(TAG, "Rendering part with " + obj.getNumVertices()
                + " vertices and " + textureName);
        //Log.d("CreateRendererStream",""+textureStream);
        Log.d("IsPartsAvalible",""+isPartsAvalible);
        if(isPartsAvalible==true)
        {
            ObjectRenderer partsRenderer=new ObjectRenderer();
            partsRenderer.createOnGlThread(context,obj,expansionFile,textureName,materialname);
            if(materialname.equals("Transprent_Head_001")||materialname.equals("Transprent_Head_001_Green.png.004.png.001.png")
                    ||materialname.equals("Head")||materialname.equals("LightYellow_transprent")||materialname.equals("Transprent_blue")||materialname.equals("Red_transeprent"))
            {
                Log.d("TheIfMaterialName",materialname);
                partsRenderer.setTransparencyMode(true);
                //partsRenderer.setMaterialProperties(1f, 0.8f, 0f, 1.0f);
            }
            else if(mtlFileName.equals("tissue_parts.mtl"))
            {
                if(materialname.equals("Material.001")||materialname.equals("Material.003")||materialname.equals("Material.004")||materialname.equals("Transparency"))
                {
                    partsRenderer.setTransparencyMode(true);
                    //partsRenderer.setMaterialProperties(1f, 0.8f, 0f, 1.0f);
                }
            }
            materialGroupObjectRenders1.add(partsRenderer);
        }
        else {
            ObjectRenderer objectRenderer = new ObjectRenderer();
            objectRenderer.createOnGlThread(context, obj, expansionFile, textureName,materialname);
             if(materialname.equals("Bacteriophage:Transprent_Head")||materialname.equals("Transprent_Head")
                    ||materialname.equals("Transprent_Head_002")||materialname.equals("LightYellow_transprent")||materialname.equals("Transprent_blue")||materialname.equals("Red_transeprent"))
            {
                Log.d("TheIfMaterialName",materialname);
                objectRenderer.setTransparencyMode(true);
                //objectRenderer.setMaterialProperties(1f, 0.8f, 0f, 1.0f);
            }
            else if(mtlFileName.equals("tissue.mtl"))
            {
                if(materialname.equals("Material.001")||materialname.equals("Material.003")||materialname.equals("Material.004")||materialname.equals("Transparency"))
                {
                    objectRenderer.setTransparencyMode(true);
                    //objectRenderer.setMaterialProperties(1f, 0.8f, 0f, 1.0f);
                }
            }

            materialGroupObjectRenderers.add(objectRenderer);
        }
        //objectRenderer.setBlendMode(ObjectRenderer.BlendMode.Grid);


    }

    public void draw(float[] cameraView, float[] cameraPerspective,
                     float lightIntensity,float[] AnchorMatrix,float scalfactor,int isPartsClicked)
    {
        if(isPartsClicked==1)
        {
            for (ObjectRenderer renderer : materialGroupObjectRenders1)
            {

                renderer.updateModelMatrix(AnchorMatrix,scalfactor);
                renderer.draw(cameraView, cameraPerspective, lightIntensity);
            }
        }
        else {
            for (ObjectRenderer renderer : materialGroupObjectRenderers) {
                renderer.updateModelMatrix(AnchorMatrix, scalfactor);
                renderer.draw(cameraView, cameraPerspective, lightIntensity);
            }
        }


    }

    public void initializePath(Context context,String obbPath,String objpath,String mtlpath) {
        try {
            Log.d("PrintObbPath",obbPath);
            expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context, 1, 0, obbPath);
            Log.d("TheObjName",""+objpath+" "+","+" "+mtlpath);
            Log.d("PrintExpanisonFile",""+expansionFile);
            ZipResourceFile.ZipEntryRO[] entriesarray = expansionFile.getAllEntries();
            for (ZipResourceFile.ZipEntryRO entry : entriesarray) {
                try {
                    if (entry.mFileName.endsWith(objpath)) {
                        Log.d("PrintTheObjObb",entry.mFileName);
                        modelStram = new DataInputStream(expansionFile.getInputStream(entry.mFileName));
                    }
                    if (entry.mFileName.endsWith(mtlpath)) {
                        Log.d("PrintTheObjObb",entry.mFileName);
                        mtlStream = new DataInputStream(expansionFile.getInputStream(entry.mFileName));
                    }
                    Log.d("FileZipName", entry.mFileName);
                } finally {
                    if (null != modelStram) {
                        Log.d("ModelStream", modelStram.toString());
                        //modelStram.close();
                    }
                    if (null != mtlStream) {
                        Log.d("mtlStream", mtlStream.toString());
                        //mtlStream.close();
                    }
                }
            }
            Log.d("PrintModelStraem",""+modelStram);
            utils.setZipFIle(expansionFile);
            utils.setInputStream(mtlStream);
            try {
                ZipResourceFile.ZipEntryRO[] entries = expansionFile.getAllEntries();
                long totalCompressedLength = 0;
                for (ZipResourceFile.ZipEntryRO entry : entries) {
                    totalCompressedLength += entry.mCompressedLength;
                }
                for (ZipResourceFile.ZipEntryRO entry : entries) {
                    if (-1 != entry.mCRC32) {
                        CRC32 crc = new CRC32();
                        DataInputStream dis = null;
                        try {
                            dis = new DataInputStream(expansionFile.getInputStream(entry.mFileName));
                            Log.d("FileZipName", entry.mFileName);
                            if (crc.getValue() != entry.mCRC32) {
                                Log.e("RenderObject", "CRC does not match for entry: " + entry.mFileName);
                                Log.e("RenderObject", "In file: " + entry.getZipFileName());
                            }
                        } finally {
                            if (null != dis) {
                                dis.close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ObbException",""+e.toString());
            }
        } catch (IOException e) {
            Log.w("Test_Resource", "Failed to find expansion file", e);
        }
    }





}