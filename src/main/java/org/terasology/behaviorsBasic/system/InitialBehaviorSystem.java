/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.behaviorsBasic.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.behavior.BehaviorComponent;
import org.terasology.logic.behavior.GroupTagComponent;
import org.terasology.logic.behavior.Interpreter;
import org.terasology.logic.behavior.asset.BehaviorTree;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.registry.In;
import org.terasology.wildAnimals.component.WildAnimalComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class InitialBehaviorSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(InitialBehaviorSystem.class);
    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;

    /***
     * Objective: assign the same behavior to entities of the same type (wild animals).
     * Please modify the following code according to the instructions provided in each
     * GCI task.
     *
     * If you are on tasks Behaviors: 4 and Behavior: 5, please check the links provided
     * to know more about identifying and working with groups. Also, be sure to check
     * the WildAnimalsMadness module.
     *
     * @return success message
     */
    @Command(shortDescription = "Assigns the 'hannahsDeer' behavior to all red deer.")
    public String assignBehavior() {
        String behavior = "BehaviorsBasic:hannahsDeer";
        for (EntityRef entityRef : entityManager.getEntitiesWith(WildAnimalComponent.class)) {
            if(entityRef.getParentPrefab().getName().equals("WildAnimals:redDeer")) {
                logger.info("Assigning behavior to a wild animal based on the following prefab: " + entityRef.getParentPrefab().getName());

                assignBehaviorToEntity(entityRef, behavior);

                logger.info("Behavior assigned:" + behavior);

            }
        }
        return "All red deer animals should have the same behavior now.";
    }


    private void assignBehaviorToEntity(EntityRef entityRef, String behavior) {

        BehaviorTree newBehaviorTree = assetManager.getAsset(behavior, BehaviorTree.class).get();

        if(null != newBehaviorTree) {
            BehaviorComponent behaviorComponent = new BehaviorComponent();

            behaviorComponent.tree = newBehaviorTree;
            behaviorComponent.interpreter = new Interpreter(new Actor(entityRef));
            behaviorComponent.interpreter.setTree(newBehaviorTree);

            entityRef.saveComponent(behaviorComponent);
        }

    }

    @Command(shortDescription = "Assigns the 'hannahsDeer' behavior to all deer.")
    public String assignGroupBehavior() {
        String behavior = "BehaviorsBasic:hannahsDeer";
        for (EntityRef entityRef : entityManager.getEntitiesWith(GroupTagComponent.class)) {
            if (entityRef.getComponent(GroupTagComponent.class).groups.contains("gci")) {
                BehaviorComponent behaviorComponent = entityRef.getComponent(BehaviorComponent.class);
                GroupTagComponent groupTagComponent = entityRef.getComponent(GroupTagComponent.class);
                groupTagComponent.backupBT = behaviorComponent.tree;
                groupTagComponent.backupRunningState = new Interpreter(behaviorComponent.interpreter);
                entityRef.saveComponent(groupTagComponent);

                assignBehaviorToEntity(entityRef, behavior);
            }
        }
        return "All members in the gci group now have the same behavior!";
    }
}
