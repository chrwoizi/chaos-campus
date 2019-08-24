using System;
using System.Windows.Forms;
using System.Collections.Generic;
using EditorObjects;
using System.IO;
using WeifenLuo.WinFormsUI.Docking;
using Javy.Controls;
using System.Drawing;

namespace Javy
{
    /// <summary>
    ///  The main MDI Application
    /// </summary>
    public partial class EditorMDI : Form
    {
        private int childFormNumber = 0;
        
        // Deserializer for docking manager
        private DeserializeDockContent m_deserializeDockContent;
       
        // Initialize Toolboxes
        private TilesetToolForm tilesetToolbox;
        private SoundToolForm soundToolbox;
        private UndoToolForm undoToolbox;
        private BrushToolForm brushToolbox;
        private MapObjectInspectorToolForm mapObjectInspectorToolbox;

        /// <summary>
        /// Initializes a new instance of the <see cref="EditorMDI"/> class.
        /// </summary>
        public EditorMDI()
        {
            InitializeComponent();
            tilesetToolbox = new TilesetToolForm();
            soundToolbox = new SoundToolForm();
            undoToolbox = new UndoToolForm();
            brushToolbox = new BrushToolForm();
            mapObjectInspectorToolbox = new MapObjectInspectorToolForm(tilesetToolbox.TileControl, brushToolbox.BrushOptions);
            //SettingsManager.GetInstance().Load();
        }

        /// <summary>
        /// Asks the user for properties of the newcreated map.
        /// </summary>
        /// <param name="sender">The sender.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void ShowNewForm(object sender, EventArgs e)
        {
            // Show NewMapForm
            //  -Ask user for height/widht and other details of the new map
            NewMapForm mapprop = new NewMapForm();
            // set standard tiles:
            // TODO for Tobias: e.g.
            QuickTile tg1 = tilesetToolbox.TileControl.QuickTile1;
            //if (tg1.TilesetId != 0)
            //{
                mapprop.Tile1 = tilesetToolbox.TileControl.Tilesets[tg1.TilesetId].GetTile(tg1.TileId);
            //}
            QuickTile tg2 = tilesetToolbox.TileControl.QuickTile2;
            mapprop.Tile2 = tilesetToolbox.TileControl.Tilesets[tg2.TilesetId].GetTile(tg2.TileId);
            QuickTile tg3 = tilesetToolbox.TileControl.QuickTile3;
            mapprop.Tile3 = tilesetToolbox.TileControl.Tilesets[tg3.TilesetId].GetTile(tg3.TileId);
            QuickTile tg4 = tilesetToolbox.TileControl.QuickTile4;
            mapprop.Tile4 = tilesetToolbox.TileControl.Tilesets[tg4.TilesetId].GetTile(tg4.TileId);
            

            if (mapprop.ShowDialog(this) == DialogResult.OK)
            {
                QuickTile tg;
                switch (mapprop.GraphicSelected)
                {
                    case 1:
                        tg = tg1;
                        break;
                    case 2:
                        tg = tg2;
                        break;
                    case 3:
                        tg = tg3;
                        break;
                    case 4:
                        tg = tg4;
                        break;

                    default:
                        tg = new QuickTile(0, 0);
                        break;
                }

              
                //if (tg.TilesetId == 0) tg.TilesetId = 1;

                Map map = new Map(mapprop.MapWidth, mapprop.MapHeight,tg.ToGraphicId());
                map.Author = mapprop.MapAuthor;
                map.Version = mapprop.MapVersion;
                CreateMapForm("Map " + childFormNumber++, map);
            }            
        }

        private void CreateMapForm(string Caption, Map Map)
        {
            // Create a new instance of the child form.
            MapForm childForm = new MapForm();
            // Make it a child of this MDI form before showing it.

            childForm.MdiParent = this;
            childForm.Text = Caption;
            childForm.EditorMDI = this;
            childForm.BrushOptions = brushToolbox.BrushOptions;
            childForm.TileControl = tilesetToolbox.TileControl;
            childForm.MapObjectInspector = mapObjectInspectorToolbox.MapObjectInspector;

            childForm.Map = Map;
            childForm.UndoControl = undoToolbox.UndoControl;
            childForm.Show(dockPanel);
        }

        /// <summary>
        /// Opens the file.
        /// </summary>
        /// <param name="sender">The sender.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void OpenFile(object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();
            //openFileDialog.InitialDirectory = 
            //    SettingsManager.GetInstance()["OpenFileDialog-Path"].Get(
            //        Environment.GetFolderPath(Environment.SpecialFolder.Personal));
            // added correct .NET Property Settings
            openFileDialog.InitialDirectory = Properties.Settings.Default.PathToMaps;

            openFileDialog.Filter = "Level Files (*.lvl)|*.lvl|All Files (*.*)|*.*";
            if (openFileDialog.ShowDialog(this) == DialogResult.OK)
            {
                string FileName = openFileDialog.FileName;
                //SettingsManager.GetInstance()["OpenFileDialog-Path"].Set(Path.GetDirectoryName(openFileDialog.FileName));
                
                // save the Open-Maps-Path to the settings
                Properties.Settings.Default.PathToMaps = Path.GetDirectoryName(openFileDialog.FileName);
                
                if (FileName.EndsWith("lvl"))
                {
                    MapManagerXML MapManager = new MapManagerXML();
                    Map map = MapManager.Load(FileName);
                    if (map != null)  // Error occured while loading
                    {
                        // Checking for tilesets

                        List<uint> list = new List<uint>();
                        for (int y = 0; y < map.Height; y++)        // Scan mapData for used tiles
                        {
                            for (int x = 0; x < map.Width; x++)
                            {
                                uint graphicID = (map.GetField(x, y).GetGraphicID()) >> 16;
                                int index = list.IndexOf(graphicID);
                                if (index == -1)            // Tile not yet in list
                                {
                                    list.Add(graphicID);
                                }
                            }
                        }
                        int ind;
                        // Scan map objects for used tiles
                        IMapObject[] mapobjects = map.GetObjects();
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
                        string message = "";
                        bool ready = true;
                        foreach (uint tileset in list)
                        {
                            if (!(tilesetToolbox.TileControl.isLoaded((int)tileset)))
                            {
                                ready = false;
                                message = message + tileset.ToString() + " ";
                            }
                        }
                        if (ready)                  // All tilesets l
                        {
                            CreateMapForm(FileName, map);
                            /*

                            MapForm childForm = new MapForm();
                           
                            childForm.Map = map;
                            
                            
                            childForm.MdiParent = this;
                            childForm.Text = FileName;// "Map " + childFormNumber++;
                            childForm.Show(dockPanel);
                            childForm.EditorMDI = this;
                            childForm.TileControl = tilesetToolbox.TileControl;
                            childForm.BrushOptions = brushToolbox.BrushOptions;
                            childForm.UndoControl = undoToolbox.UndoControl;
                            childForm.MapObjectInspector = mapObjectInspectorToolbox.MapObjectInspector;
                           */



                        }
                        else                    // Missing tilesets
                        {
                            //MessageBox.Show("Missing tilesets:" + message);
                            // Todo: Add dialog to allow tileset substitution
                            MapForm childForm = new MapForm();
                            childForm.Map = map;
                            MissingTilesetForm missingTilesetForm = new MissingTilesetForm(childForm.MapControl, tilesetToolbox.TileControl);
                            DialogResult result = missingTilesetForm.ShowDialog();
                            if (result == DialogResult.OK)
                            {
                                childForm.MdiParent = this;
                                childForm.Text = FileName;// "Map " + childFormNumber++;
                                childForm.Show(dockPanel);
                                childForm.EditorMDI = this;
                                childForm.TileControl = tilesetToolbox.TileControl;
                                childForm.BrushOptions = brushToolbox.BrushOptions;
                                childForm.UndoControl = undoToolbox.UndoControl;
                                childForm.MapObjectInspector = mapObjectInspectorToolbox.MapObjectInspector;
                                
                            }
                        }

                    }
                    else
                    {
                        MessageBox.Show("Invalid file!");
                    }
                }
                else
                {
                    MessageBox.Show("Unsupported file format!");
                }
                // TODO: Add more file formats and error checking.
            }
        }
        /// <summary>
        /// Handles the Click event of the SaveAsToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void SaveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SaveFileDialog saveFileDialog = new SaveFileDialog();
            saveFileDialog.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            saveFileDialog.Filter = "Level Files (*.lvl)|*.lvl|All Files (*.*)|*.*";
            if (saveFileDialog.ShowDialog(this) == DialogResult.OK)
            {
                string FileName = saveFileDialog.FileName;
                if (saveFileDialog.FileName.EndsWith("lvl"))
                {
                    MapManagerXML MapManager = new MapManagerXML();
                    MapForm activeForm = (MapForm)this.ActiveMdiChild;
                    if (activeForm == null) MessageBox.Show( "Null!");
                    activeForm.Text = FileName;
                    MapManager.Save(FileName, activeForm.Map, null);
                    activeForm.Map.Levelfilename = FileName;
                }
                else
                {
                    MessageBox.Show("Unsupported file format!");
                }
                
            }
        }

        /// <summary>
        /// Handles the Click event of the ExitToolsStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void ExitToolsStripMenuItem_Click(object sender, EventArgs e)
        {
            //Application.Exit();
            Close();
        }

        /// <summary>
        /// Handles the Click event of the CutToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void CutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).EditCopy();
            ((MapForm)ActiveMdiChild).EditDelete();
        }

        /// <summary>
        /// Handles the Click event of the CopyToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void CopyToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).EditCopy();
        }

        /// <summary>
        /// Handles the Click event of the PasteToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void PasteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).EditPaste();
        }

        /// <summary>
        /// Handles the Click event of the ToolBarToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void ToolBarToolStripMenuItem_Click(object sender, EventArgs e)
        {
            toolStrip.Visible = toolBarToolStripMenuItem.Checked;
        }

        /// <summary>
        /// Handles the Click event of the StatusBarToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void StatusBarToolStripMenuItem_Click(object sender, EventArgs e)
        {
            statusStrip.Visible = statusBarToolStripMenuItem.Checked;
        }

        /// <summary>
        /// Handles the Click event of the CascadeToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void CascadeToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.Cascade);
        }

        /// <summary>
        /// Handles the Click event of the TileVerticleToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TileVerticleToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.TileVertical);
        }

        /// <summary>
        /// Handles the Click event of the TileHorizontalToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void TileHorizontalToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.TileHorizontal);
        }

        /// <summary>
        /// Handles the Click event of the ArrangeIconsToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void ArrangeIconsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.ArrangeIcons);
        }

        /// <summary>
        /// Handles the Click event of the CloseAllToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void CloseAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            foreach (Form childForm in MdiChildren)
            {
                childForm.Close();
            }
        }

        /// <summary>
        /// Handles the Load event of the EditorMDI control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void EditorMDI_Load(object sender, EventArgs e)
        {


            #region Load Dockingmanager Settings
            // create a deserializer
            m_deserializeDockContent = new DeserializeDockContent(GetContentFromPersistString);
            
            // set config file for windowpositions
            string configFile = Path.Combine(Path.GetDirectoryName(Application.ExecutablePath), "ToolboxPositions.config");


            // show toolwindows
            tilesetToolbox.Show(this.dockPanel);
            soundToolbox.Show(this.dockPanel);
            undoToolbox.Show(this.dockPanel);
            brushToolbox.Show(this.dockPanel);
            mapObjectInspectorToolbox.Show(this.dockPanel);

            // Hab hier mal die Reihenfolge der Shows und load-config vertauscht, weil in der 
            // DockToDefaultPosition() eine Exception ausgeschmissen wurde, dass tilesetToolbox 
            // noch nicht instanziert sei. (Markus)

            // load config to the docking manager
            if (File.Exists(configFile))
            {
                try
                {
                    dockPanel.LoadFromXml(configFile, m_deserializeDockContent);
                }
                catch
                {
                    DockToDefaultPosition();
                }
            }
            else
            {
                DockToDefaultPosition();
            }
            #endregion

         }

        private void DockToDefaultPosition()
        {
            // this section docks all toolwindows to its default position
            tilesetToolbox.DockState = DockState.DockLeft;
            soundToolbox.DockState = DockState.DockLeft;
            undoToolbox.DockState = DockState.DockLeft;
            brushToolbox.DockState = DockState.DockRight;
            mapObjectInspectorToolbox.DockState = DockState.DockRight;
        }


        /// <summary>
        /// Gets the content from persist string.
        /// </summary>
        /// <param name="persistString">The persist string.</param>
        /// <returns></returns>
        private DockContent GetContentFromPersistString(string persistString)
        {
            // deserialize data from config
            if (persistString == typeof(TilesetToolForm).ToString())
                return tilesetToolbox;
            else if (persistString == typeof(SoundToolForm).ToString())
                return soundToolbox;
            else if (persistString == typeof(UndoToolForm).ToString())
                return undoToolbox;
            else if (persistString == typeof(BrushToolForm).ToString())
                return brushToolbox;
            else if (persistString == typeof(MapObjectInspectorToolForm).ToString())
                return mapObjectInspectorToolbox;
            return null;
        }

        /// <summary>
        /// Handles the Click event of the exportAsToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void exportAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SaveFileDialog saveFileDialog = new SaveFileDialog();
      
            // added correct .NET 2.0 property settings:
            saveFileDialog.InitialDirectory = Properties.Settings.Default.PathToMaps;

            saveFileDialog.Filter = "Exported map (*.map)|*.map|All Files (*.*)|*.*";
            if (saveFileDialog.ShowDialog(this) == DialogResult.OK)
            {
                string FileName = saveFileDialog.FileName;
                if (saveFileDialog.FileName.EndsWith("map"))
                {
                    MapManagerBinary MapManager = new MapManagerBinary();
                    MapForm activeForm = (MapForm)this.ActiveMdiChild;
                    if (activeForm == null) return;
                    MapManager.Save(FileName, activeForm.Map, tilesetToolbox.TileControl.Tilesets);
                }
                else
                {
                    MessageBox.Show("Unsupported file format!");
                }
                
            }
        }

        /// <summary>
        /// Handles the Click event of the saveToolStripMenuItem control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MapForm activeForm = (MapForm)this.ActiveMdiChild;
            if (activeForm == null) return;
            if (activeForm.Map.Levelfilename==null)
            {
                SaveAsToolStripMenuItem_Click(sender, e);
            }
            else
            {
                MapManagerXML MapManager = new MapManagerXML();
                MapManager.Save(activeForm.Map.Levelfilename, activeForm.Map, null);
            }
        }

        private void viewMenu_DropDownOpening(object sender, EventArgs e)
        {
            // Map Optionen aktualisieren
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm))
            {
                showGridToolStripMenuItem.Checked = false;
                showCollisionToolStripMenuItem.Checked = false;
                showObjectsToolStripMenuItem.Checked = false;
                showRulersToolStripMenuItem.Checked = false;

                showGridToolStripMenuItem.Enabled = false;
                showCollisionToolStripMenuItem.Enabled = false;
                showObjectsToolStripMenuItem.Enabled = false;
                showRulersToolStripMenuItem.Enabled = false;
                return;
            }
            showGridToolStripMenuItem.Enabled = true;
            showCollisionToolStripMenuItem.Enabled = true;
            showObjectsToolStripMenuItem.Enabled = true;
            showRulersToolStripMenuItem.Enabled = true;

            showGridToolStripMenuItem.Checked = ((MapForm)ActiveMdiChild).ShowGrid;
            showCollisionToolStripMenuItem.Checked = ((MapForm)ActiveMdiChild).ShowCollisions;
            showObjectsToolStripMenuItem.Checked = ((MapForm)ActiveMdiChild).ShowObjects;
            showRulersToolStripMenuItem.Checked = ((MapForm)ActiveMdiChild).ShowRulers;
        }

        private void showGridToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).ShowGrid = showGridToolStripMenuItem.Checked;
        }

        private void showCollisionToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).ShowCollisions = showCollisionToolStripMenuItem.Checked;
        }

        private void showObjectsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).ShowObjects = showObjectsToolStripMenuItem.Checked;
        }

        private void showRulersToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).ShowRulers = showRulersToolStripMenuItem.Checked;
        }

        private void tilesetToolboxToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // Bug im Dockingmanager: es müssen Show und Hide aufgerufen werden
            if (tilesetToolboxToolStripMenuItem.Checked)
                tilesetToolbox.Show();
            else
                tilesetToolbox.Hide();
        }

        private void soundToolboxToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // Bug im Dockingmanager: es müssen Show und Hide aufgerufen werden
            if (soundToolboxToolStripMenuItem.Checked)
                soundToolbox.Show();
            else
                soundToolbox.Hide();
        }

        private void undoToolboxToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // Bug im Dockingmanager: es müssen Show und Hide aufgerufen werden
            if (undoToolboxToolStripMenuItem.Checked)
                undoToolbox.Show();
            else
                undoToolbox.Hide();
        }

        private void brushToolboxToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // Bug im Dockingmanager: es müssen Show und Hide aufgerufen werden
            if (brushToolboxToolStripMenuItem.Checked)
                brushToolbox.Show();
            else
                brushToolbox.Hide();
        }

        private void objectinspectorToolboxToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // Bug im Dockingmanager: es müssen Show und Hide aufgerufen werden
            if (objectinspectorToolboxToolStripMenuItem.Checked)
                mapObjectInspectorToolbox.Show();
            else
                mapObjectInspectorToolbox.Hide();
        }

        private void toolboxesToolStripMenuItem_DropDownOpening(object sender, EventArgs e)
        {
            // Toolbox Optionen aktualisieren
            tilesetToolboxToolStripMenuItem.Checked = tilesetToolbox.Visible;
            soundToolboxToolStripMenuItem.Checked = soundToolbox.Visible;
            undoToolboxToolStripMenuItem.Checked = undoToolbox.Visible;
            brushToolboxToolStripMenuItem.Checked = brushToolbox.Visible;
            objectinspectorToolboxToolStripMenuItem.Checked = mapObjectInspectorToolbox.Visible;
            // ToDo: Visible ist falsch!
        }

        private void selectAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).EditMarkAll();
        }

        private void deleteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).EditDelete();
        }

        /// <summary>
        /// Runs the actual map in the emulator
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void runMap_Click(object sender, EventArgs e)
        {
            // Get path of the executable
            String appDir = Path.GetDirectoryName(Application.ExecutablePath);

            // Export actual view to .\level\StartLevel.map
            MapManagerBinary MapManager = new MapManagerBinary();
            MapForm activeForm = (MapForm)this.ActiveMdiChild;
            if (activeForm == null) return;
            Directory.CreateDirectory(appDir + "\\level");
            MapManager.Save(appDir + "\\level\\StartLevel.map", activeForm.Map, tilesetToolbox.TileControl.Tilesets);

            
            // Run zip program to add level-data to .jar archive
            try
            {
                System.Diagnostics.Process proc = new System.Diagnostics.Process();
                proc.EnableRaisingEvents = false;
                proc.StartInfo.WorkingDirectory = appDir;
                proc.StartInfo.UseShellExecute = false;
                proc.StartInfo.RedirectStandardOutput = true;
                proc.StartInfo.FileName = "zip.exe";
                proc.StartInfo.Arguments = "-r prototype.jar level";
                proc.StartInfo.CreateNoWindow = true;
                proc.Start();
                proc.WaitForExit();
            }
            catch(Exception ex)
            {
                string message = "I am not able to find a compression tool in your program folder." + System.Environment.NewLine;
                message += "Verify that you have zip.exe in your programfolder!";
                message += System.Environment.NewLine + ex.Message;
                MessageBox.Show(message, "We have a Problem:",MessageBoxButtons.OK,MessageBoxIcon.Information);
                return;
            }


            if (!File.Exists(appDir + "\\prototype.jar"))
            {
                MessageBox.Show("Error while compiling the map.", "Error:", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            // Create .jad-file with correct size of .jar-file
            FileInfo f = new FileInfo(appDir + "\\prototype.jar");
            string strText = "MIDlet-1: javy prototype, , GameMIDlet\nMIDlet-Jar-Size: " + f.Length ;
            strText += "\nMIDlet-Jar-URL: prototype.jar\nMIDlet-Name: GameMIDlet\nMIDlet-Vendor: Midlet Suite Vendor\n";
            strText += "MIDlet-Version: 1.0.0\nMicroEdition-Configuration: CLDC-1.1\nMicroEdition-Profile: MIDP-2.0";
            File.WriteAllText(appDir + "\\prototype.jad", strText);

            // Finally run the emulater from the wtk directory
            try
            {
                System.Diagnostics.Process proc = new System.Diagnostics.Process();
                proc = new System.Diagnostics.Process();
                proc.EnableRaisingEvents = false;
                proc.StartInfo.WorkingDirectory = appDir;
                proc.StartInfo.UseShellExecute = false;
                proc.StartInfo.CreateNoWindow = true;
                proc.StartInfo.FileName = Javy.Properties.Settings.Default.wtkDirectory +
                                          "\\bin\\emulator.exe";
                proc.StartInfo.Arguments = "-Xdescriptor \"" + appDir + "\\prototype.jad\"";
                proc.Start();
           
            }
            catch(Exception ex)
            {
                string message = "I can't find the emulator of the Java WTK!" + System.Environment.NewLine;
                message += "Verify that you have setup the correct path for the WTK25 in your Leveleditor.exe.config file.";
                message += System.Environment.NewLine + ex.Message;
                MessageBox.Show(message, "We have a Problem:", MessageBoxButtons.OK, MessageBoxIcon.Information);
                OptionsForm options = new OptionsForm();
                options.ShowDialog();
            }
        }

        private void settingsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OptionsForm options = new OptionsForm();   
            options.ShowDialog();
        }

        private void undoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).EditUndo();
        }

        private void redoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).EditRedo();
        }

        public int CursorPosX
        {
            set
            {
                if (value < 0)
                    cursorPosX.Text = "X: -";
                else
                    cursorPosX.Text = "X: " + value;
            }
        }
        public int CursorPosY
        {
            set
            {
                if (value < 0)
                    cursorPosY.Text = "Y: -";
                else
                    cursorPosY.Text = "Y: "+value;
            }
        }

        private void toolStripMenuItemQ_Click(object sender, EventArgs e)
        {
            int id = 0;
            if (sender == toolStripMenuItemQ1) id = 1;
            if (sender == toolStripMenuItemQ2) id = 2;
            if (sender == toolStripMenuItemQ3) id = 3;
            if (sender == toolStripMenuItemQ4) id = 4;

            tilesetToolbox.TileControl.QuickTile = id;
        }

        private void BrushToolStripMenuItem_Click(object sender, EventArgs e)
        {
            BrushType id = BrushType.None;
            if (sender == tilebrushToolStripMenuItem) id = BrushType.Background;
            if (sender == collisionbrushToolStripMenuItem) id = BrushType.Collision;
            if (sender == objectSelectorToolStripMenuItem) id = BrushType.ObjectSelector;
            if (sender == objectbrushToolStripMenuItem) id = BrushType.ObjectCreator;
            if (sender == tileselectorToolStripMenuItem) id = BrushType.BackgroundSelector;
            brushToolbox.BrushOptions.BrushType = id;
        }

        private void saveToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            // save the toolbox window positions in a config file
            string configFile = Path.Combine(Path.GetDirectoryName(Application.ExecutablePath), "ToolboxPositions.config");
            dockPanel.SaveAsXml(configFile);
        }

        private void resetToolStripMenuItem_Click(object sender, EventArgs e)
        {
            DockToDefaultPosition();
        }

        private void helpToolStripButton_Click(object sender, EventArgs e)
        {
            Help.ShowHelp(this,"LevelEditorHelp.chm");
        }

        private void replaceTilesetToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ReplaceTilesetsForm replaceForm = new ReplaceTilesetsForm(((MapForm)ActiveMdiChild).MapControl, tilesetToolbox.TileControl);
            replaceForm.ShowDialog();
        }

        private void resizeMapToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ResizeMapForm resizeMapForm = new ResizeMapForm(((MapForm)ActiveMdiChild).MapControl);
            resizeMapForm.ShowDialog();
        }

        private void fullscreenToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;
            ((MapForm)ActiveMdiChild).Fullscreen = !((MapForm)ActiveMdiChild).Fullscreen;
        }

        private void EditorMDI_FormClosed(object sender, FormClosedEventArgs e)
        {
            // Save Settings
            Properties.Settings.Default.Save();
            
        }

        private void saveScreenshotToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveMdiChild == null || !(ActiveMdiChild is MapForm)) return;

            SaveFileDialog saveFileDialog = new SaveFileDialog();
            saveFileDialog.InitialDirectory = Properties.Settings.Default.PathToMaps;
            saveFileDialog.Filter = "Portable Network Graphics (*.png)|*.png|All Files (*.*)|*.*";
            if (saveFileDialog.ShowDialog(this) == DialogResult.OK)
            {
                Bitmap s = ((MapForm)ActiveMdiChild).Screenshot();
                s.Save(saveFileDialog.FileName);
                System.Diagnostics.Process.Start(saveFileDialog.FileName);
            }
        }

        private void aboutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            AboutBox about = new AboutBox();
            about.ShowDialog();
           
        }

    }
}
