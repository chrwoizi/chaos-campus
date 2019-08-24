using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using EditorObjects;
using EditorObjects.MapObjects;

namespace Javy.Controls.Brushes
{
    public partial class BrushOpObject : UserControl
    {
        private string ObjectStatic = "Static Object";
        private string ObjectEnemy = "Enemy / NPC";
        private string ObjectDoor = "Door";
        private string ObjectContainer = "Container";
        private string ObjectBreakable = "Breakable";
        private string ObjectMovable = "Movable";
        private string ObjectItem = "Item";
        private string ObjectSound = "Sound";
        private string ObjectDamager = "Damager";
        private string ObjectTrigger = "Trigger Story";
        private string ObjectTriggerEnabler = "Trigger Enabler";
        private string ObjectTriggerDoor = "Trigger Door";
        private string ObjectTriggerDoorSound = "Trigger Door Sound";
        private string ObjectTriggerContainer = "Trigger Container";
        private string ObjectTriggerContainerSound = "Trigger Container Sound";
        private string ObjectTriggerExit = "Trigger Exit";
        private string ObjectTriggerTeleport = "Trigger Teleport";
        private string ObjectTriggerComment = "Trigger Comment";
        private string ObjectMapStart = "Map Start Position";
        

        public BrushOpObject()
        {
            InitializeComponent();
            objectType.Items.Add(ObjectStatic);
            objectType.Items.Add(ObjectEnemy);
            objectType.Items.Add(ObjectDoor);
            objectType.Items.Add(ObjectContainer);
            objectType.Items.Add(ObjectBreakable);
            objectType.Items.Add(ObjectMovable);
            objectType.Items.Add(ObjectItem);
            objectType.Items.Add(ObjectSound);
            objectType.Items.Add(ObjectDamager);
            objectType.Items.Add(ObjectTrigger);
            objectType.Items.Add(ObjectTriggerEnabler);
            objectType.Items.Add(ObjectTriggerDoor);
            objectType.Items.Add(ObjectTriggerDoorSound);
            objectType.Items.Add(ObjectTriggerContainer);
            objectType.Items.Add(ObjectTriggerContainerSound);
            objectType.Items.Add(ObjectTriggerExit);
            objectType.Items.Add(ObjectTriggerTeleport);
            objectType.Items.Add(ObjectTriggerComment);
            objectType.Items.Add(ObjectMapStart);
            objectType.SelectedIndex = 0;
        }

        public IMapObject GenerateObject()
        {
            IMapObject obj = null;
            if (objectType.SelectedItem.ToString() == ObjectStatic)
            {
                obj = new StaticMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectTrigger)
            {
                obj = new TriggerMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectEnemy)
            {
                obj = new EnemyMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectDoor)
            {
                obj = new DoorMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectContainer)
            {
                obj = new ContainerMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectBreakable)
            {
                obj = new BreakableMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectMovable)
            {
                obj = new MovableMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectItem)
            {
                obj = new ItemMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectSound)
            {
                obj = new SoundMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectDamager)
            {
                obj = new DamagerMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectEnemy)
            {
                obj = new EnemyMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectTriggerDoor)
            {
                obj = new TriggerDoorMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectTriggerContainer)
            {
                obj = new TriggerContainerMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectTriggerDoorSound)
            {
                obj = new TriggerDoorSoundMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectTriggerContainerSound)
            {
                obj = new TriggerContainerSoundMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectTriggerExit)
            {
                obj = new TriggerExitMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectTriggerTeleport)
            {
                obj = new TriggerTeleportMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectTriggerEnabler)
            {
                obj = new TriggerEnablerMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectTriggerComment)
            {
                obj = new TriggerCommentMapObject();
            }
            if (objectType.SelectedItem.ToString() == ObjectMapStart)
            {
                obj = new MapStart();
            }
            obj.SizeX = 1;
            obj.SizeY = 1;
            return obj;
        }
    }
}
