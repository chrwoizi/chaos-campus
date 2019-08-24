using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using EditorObjects;

namespace Javy.Controls
{
    public partial class MapObjectInspector : UserControl
    {
        public MapObjectInspector()
        {
            InitializeComponent();
            mapControl1.AllowTiles = true;
            mapControl1.AllowCollisions = false;
            mapControl1.AllowObjects = false;
            mapControl1.EditNotifyHandler += new EventHandler(DoMapNotify);
        }

        private IMapObject orgObject = null;

        /// <summary>
        /// Gets or sets the selected MapObject.
        /// </summary>
        /// <value>The selected object.</value>
        public IMapObject SelectedObject
        {
            get
            {
                return orgObject;
            }
            set
            {
                if (value == null)
                {
                    Inspector.SelectedObject = null;
                    mapControl1.Map = null;
                    orgObject = null;
                    return;
                }

                
                
                Inspector.SelectedObject = (IMapObject)value.Clone();
                orgObject = null; // Damit kein Notify beim AssignMap aufgerufen wird
                AssignMap(value);
                orgObject = value;
            }
        }

        private void AssignMap(IMapObject obj)
        {
            // Object is a StaticMapObject
            if (obj is StaticMapObject)
            {
                //Assign TilesMap to MapControl for simple editing
                mapControl1.Map = ((StaticMapObject)obj).Tiles;
            }
            else if (obj is DoorMapObject)
            {
                mapControl1.Map = ((DoorMapObject)obj).Tiles;
            }
            else if (obj is BreakableMapObject)
            {
                mapControl1.Map = ((BreakableMapObject)obj).Tiles;
            }
            else if (obj is MovableMapObject)
            {
                mapControl1.Map = ((MovableMapObject)obj).Tiles;
            }
            else if (obj is ContainerMapObject)
            {
                mapControl1.Map = ((ContainerMapObject)obj).Tiles;
            }

            else
            {
                //Otherwise clear the possibly filled MapControl on reselection
                mapControl1.Map = null;
            }
        }

        public TileControl TileControl
        {
            set
            {
                this.mapControl1.TileControl = value;
            }
        }

        public BrushOptions BrushOptions
        {
            set
            {
                this.mapControl1.BrushOptions = value;
            }
        }

        public delegate void MapObjectUpdateEventHandler(object sender, MapObjectEventArgs e);

        // Zur Benachrichtigung über alle Updates am aktuellen Objekt (zB. Größenänderung, Tiles, usw)
        public event MapObjectUpdateEventHandler MapBeforeUpdateHandler = null;
        public event MapObjectUpdateEventHandler MapAfterUpdateHandler = null;

        private void Inspector_PropertyValueChanged(object s, PropertyValueChangedEventArgs e)
        {
            if (orgObject != null)
            {
                if (MapBeforeUpdateHandler != null)
                    MapBeforeUpdateHandler(this, new MapObjectEventArgs(orgObject));
                
                IMapObject tmp = (IMapObject)Inspector.SelectedObject;
                Inspector.SelectedObject = tmp.Clone();
                orgObject = null;
                AssignMap(tmp);
                orgObject = tmp;
                if (MapAfterUpdateHandler != null)
                    MapAfterUpdateHandler(this, new MapObjectEventArgs(orgObject));
            }
            IMapObject t = orgObject;
            orgObject = null;
            mapControl1.Map = mapControl1.Map;
            orgObject = t;
        }

        public void DoMapNotify(object s, EventArgs e)
        {
            if (orgObject != null)
            {
                Inspector.SelectedObject = orgObject.Clone();
                if (MapBeforeUpdateHandler != null)
                    MapBeforeUpdateHandler(this, new MapObjectEventArgs(orgObject));
                if (MapAfterUpdateHandler != null)
                    MapAfterUpdateHandler(this, new MapObjectEventArgs(orgObject));
            }
        }
    }

    public class MapObjectEventArgs : EventArgs
    {
        public IMapObject UpdateObject;

        public MapObjectEventArgs(IMapObject UpdateObject)
        {
            this.UpdateObject = UpdateObject;
        }
    }
}
