using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using EditorObjects;

namespace Javy
{
    public partial class MissingTilesetForm : Form
    {
        private MapControl mapControl = null;
        private uint[,] replacement;
        public MissingTilesetForm(MapControl mapControl, TileControl tileControl)
        {
            InitializeComponent();
            this.mapControl = mapControl;
            List<uint> list = mapControl.Map.UsedTilesetIDs;
            int ind;
            IMapObject[] mapobjects = mapControl.Map.GetObjects();
            foreach (IMapObject obj in mapobjects)
            {
                byte[] data = obj.GetExportData(list);
                if (data[0] == 1)  // Static object
                {
                    StaticMapObject obj2 = (StaticMapObject)obj;
                    for (int y = 0; y < obj2.SizeY; y++)
                    {
                        for (int x = 0; x < obj2.SizeX; x++)
                        {
                            uint graphicID = (obj2.Tiles.GetField(x, y).GetGraphicID()) >> 16;
                            ind = list.IndexOf(graphicID);
                            if (ind == -1)
                            {
                                list.Add(graphicID);
                            }
                        }
                    }
                }
                if (data[0] == 4)   // Door object
                {
                    DoorMapObject obj2 = (DoorMapObject)obj;
                    ind = list.IndexOf((obj2.Graphic_id_open) >> 16);
                    if (ind == -1)
                    {
                        list.Add((obj2.Graphic_id_open) >> 16);
                    }
                    ind = list.IndexOf((obj2.Graphic_id_closed) >> 16);
                    if (ind == -1)
                    {
                        list.Add((obj2.Graphic_id_closed) >> 16);
                    }
                }
                if (data[0] == 5)   // Container object
                {
                    ContainerMapObject obj2 = (ContainerMapObject)obj;
                    ind = list.IndexOf((obj2.Graphic_id_open) >> 16);
                    if (ind == -1)
                    {
                        list.Add((obj2.Graphic_id_open) >> 16);
                    }
                    ind = list.IndexOf((obj2.Graphic_id_closed) >> 16);
                    if (ind == -1)
                    {
                        list.Add((obj2.Graphic_id_closed) >> 16);
                    }
                }
                if (data[0] == 6)   // Breakable object
                {
                    BreakableMapObject obj2 = (BreakableMapObject)obj;
                    ind = list.IndexOf((obj2.Graphic_id_open) >> 16);
                    if (ind == -1)
                    {
                        list.Add((obj2.Graphic_id_open) >> 16);
                    }
                    ind = list.IndexOf((obj2.Graphic_id_closed) >> 16);
                    if (ind == -1)
                    {
                        list.Add((obj2.Graphic_id_closed) >> 16);
                    }
                }
                if (data[0] == 7)   // Movable object
                {
                    MovableMapObject obj2 = (MovableMapObject)obj;
                    ind = list.IndexOf((obj2.Graphic_id) >> 16);
                    if (ind == -1)
                    {
                        list.Add((obj2.Graphic_id) >> 16);
                    }
                }
            }       // Done scanning used tilesets
            foreach (uint id in list)
            {
                if (!tileControl.isLoaded((int)id))
                    MissingTilesetBox.Items.Add(id);      // Get missing tilesets
              
            }
            // Get available Tilesets
            foreach (uint id in tileControl.AvaibleTilesetIds)
            {
                MapTilesetBox.Items.Add(id);
            }
            int missingCount = MissingTilesetBox.Items.Count;
            replacement=new uint[missingCount,2];
            for (int i=0; i<missingCount; i++)
            {
                replacement[i,0] = (uint)MissingTilesetBox.Items[i];
                replacement[i,1] = 0;
                MissingTilesetBox.Items[i]=MissingTilesetBox.Items[i].ToString()+" -> "+MapTilesetBox.Items[(int)replacement[i,1]].ToString();
            }
            MissingTilesetBox.SelectedIndex = 0;
            MapTilesetBox.SelectedIndex = (int)replacement[0, 1];
        }

        private void MissingTilesetBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (!MapTilesetBox.Focused)
            MapTilesetBox.SelectedIndex = (int)replacement[MissingTilesetBox.SelectedIndex, 1];
        }

        private void MapTilesetBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (!MissingTilesetBox.Focused)
            {
                int i = MissingTilesetBox.SelectedIndex;
                replacement[i, 1] = (uint)MapTilesetBox.SelectedIndex;
                MissingTilesetBox.Items[i] = replacement[i, 0].ToString() + " -> " + MapTilesetBox.Items[(int)replacement[i, 1]].ToString();
            }
        }

        private void OK_Click(object sender, EventArgs e)
        {
            Map replaceMap = mapControl.Map.CopyRegion();
            int count = 0;
            int missingCount = MissingTilesetBox.Items.Count;
            for (int i = 0; i < missingCount; i++)
            {
                count = count + replaceMap.ReplaceTilesets(replacement[i,0],replacement[i,1]);
                foreach (IMapObject obj in replaceMap.GetObjects())
                {
                    if (obj is StaticMapObject)  // Static object
                    {
                        StaticMapObject obj2 = (StaticMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets(replacement[i, 0], replacement[i, 1]);
                    }
                    if (obj is DoorMapObject)   // Door object
                    {
                        DoorMapObject obj2 = (DoorMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets(replacement[i, 0], replacement[i, 1]);
                    }
                    if (obj is ContainerMapObject)   // Container object
                    {
                        ContainerMapObject obj2 = (ContainerMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets(replacement[i, 0], replacement[i, 1]);
                    }
                    if (obj is BreakableMapObject)   // Breakable object
                    {
                        BreakableMapObject obj2 = (BreakableMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets(replacement[i, 0], replacement[i, 1]);
                    }
                    if (obj is MovableMapObject)   // Movable object
                    {
                        MovableMapObject obj2 = (MovableMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets(replacement[i, 0], replacement[i, 1]);
                    }
                }
            }
            mapControl.Map = replaceMap;        // Done replacing map tiles


            MessageBox.Show(count + " Tiles have been replaced.", "Tileset successfully replaced!",
                            MessageBoxButtons.OK, MessageBoxIcon.Information);

            DialogResult = DialogResult.OK;

            
        }

        private void Cancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
        }
    }
}