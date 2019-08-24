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
    public partial class ReplaceTilesetsForm : Form
    {
        private MapControl mapControl = null;
        public ReplaceTilesetsForm(MapControl mapControl, TileControl tileControl)
        {
            InitializeComponent();
            this.mapControl = mapControl;
            foreach (uint id in mapControl.Map.UsedTilesetIDs)
            {
                MapComboBox.Items.Add(id);
            }
            // Get avaible TilesetIDs from TileControl
            foreach (uint id in tileControl.AvaibleTilesetIds)
            {
                ReplaceComboBox.Items.Add(id);
            }
        }

        private void ReplaceButton_Click(object sender, EventArgs e)
        {
            if (MapComboBox.SelectedItem != null && ReplaceComboBox.SelectedItem != null)
            {
                // Before / After State for UndoManager
                Map replaceMap = mapControl.Map.CopyRegion();
                int count = replaceMap.ReplaceTilesets((uint)MapComboBox.SelectedItem, (uint)ReplaceComboBox.SelectedItem);

                foreach (IMapObject obj in replaceMap.GetObjects())
                {
                    if (obj is StaticMapObject)  // Static object
                    {
                        StaticMapObject obj2 = (StaticMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets((uint)MapComboBox.SelectedItem, (uint)ReplaceComboBox.SelectedItem);
                    }
                    if (obj is DoorMapObject)   // Door object
                    {
                        DoorMapObject obj2 = (DoorMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets((uint)MapComboBox.SelectedItem, (uint)ReplaceComboBox.SelectedItem);
                    }
                    if (obj is ContainerMapObject)   // Container object
                    {
                        ContainerMapObject obj2 = (ContainerMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets((uint)MapComboBox.SelectedItem, (uint)ReplaceComboBox.SelectedItem);
                    }
                    if (obj is BreakableMapObject)   // Breakable object
                    {
                        BreakableMapObject obj2 = (BreakableMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets((uint)MapComboBox.SelectedItem, (uint)ReplaceComboBox.SelectedItem);
                    }
                    if (obj is MovableMapObject)   // Movable object
                    {
                        MovableMapObject obj2 = (MovableMapObject)obj;
                        count = count + obj2.Tiles.ReplaceTilesets((uint)MapComboBox.SelectedItem, (uint)ReplaceComboBox.SelectedItem);
                    }
                }


                MessageBox.Show(count + " Tiles have been replaced.", "Tileset successfully replaced!",
                                MessageBoxButtons.OK, MessageBoxIcon.Information);
                
                mapControl.Map = replaceMap;    // Assign new Map with replacements and refresh map control
                DialogResult = DialogResult.OK;
            }
        }

    }
}